package likelion13th.SunShop.service;

//비즈니스 로직 처리 계층. 주문 비즈니스 로직
import jakarta.transaction.Transactional;
import likelion13th.SunShop.DTO.request.OrderCreateRequest;
import likelion13th.SunShop.DTO.response.OrderResponse;
import likelion13th.SunShop.domain.Item;
import likelion13th.SunShop.domain.Order;
import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.constant.OrderStatus;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.login.auth.jwt.CustomUserDetails;
import likelion13th.SunShop.repository.ItemRepository;
import likelion13th.SunShop.repository.OrderRepository;
import likelion13th.SunShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /** 주문 생성 **/
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request, CustomUserDetails user) {
        // 상품 조회
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new GeneralException(ErrorCode.ITEM_NOT_FOUND));

        // 총 금액 계산
        int totalPrice = item.getPrice() * request.getQuantity();
        // 마일리지 유효성 검사
        int mileageToUse = request.getMileageToUse();
        if (mileageToUse > user.getMaxMileage()) {
            throw new GeneralException(ErrorCode.INVALID_MILEAGE);
        }

        // 사용할 수 있는 최대 마일리지 = 총 금액
        int availableMileage = Math.min(mileageToUse, totalPrice);
        // 최종 결제 금액 계산
        int finalPrice = totalPrice - availableMileage;

        //주문 생성 ( 주문 중으로 설정은 Order.java 생성자에서 )
        Order order = Order.create(user, item, request.getQuantity(), totalPrice, finalPrice);


        //사용자 마일리지 차감 및 적립
        user.useMileage(availableMileage);
        user.addMileage((int) (finalPrice * 0.1));//결제 금액의 10% 마일리지 적립
        //최근 결제 금액 업데이트
        user.updateRecentTotal(finalPrice);

        //연관관계 설정
        user.addOrder(order);
        //주문 저장
        orderRepository.save(order);

        return OrderResponse.from(order);
    }

    /** 로그인한 사용자의 모든 주문 조회 **/
    @Transactional
    public List<OrderResponse> getAllOrders(CustomUserDetails user) {
        //프록시 객체 -> DTO로 변환 후 반환
        return user.getOrders().stream()
                .map(order -> OrderResponse.from((Order) order))
                .collect(Collectors.toList());
    }

    /** 주문 삭제 **/
    //데이터 삭제가 아니라 주문 상태 변경으로 soft delete
    //배송 완료된 상품, 주문 취소된 상품은 주문 취소 불가능
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GeneralException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.COMPLETE || order.getStatus() == OrderStatus.CANCEL) {
            throw new GeneralException(ErrorCode.ORDER_CANCEL_FAILED);
        }

        User user = order.getUser();
        // 회수해야할 마일리지보다 가지고 있는 마일리지가 적을 경우
        if (user.getMaxMileage() < (int) (order.getFinalPrice() * 0.1)) {
            throw new GeneralException(ErrorCode.INVALID_MILEAGE);
        }

        //주문 상태 변경
        order.updateStatus(OrderStatus.CANCEL);
        // 결제 시에 적립되었던 마일리지 차감 ( 결제 금액의 10% )
        user.useMileage((int)(order.getFinalPrice() * 0.1));

        //마일리지 환불
        user.addMileage(order.getTotalPrice() - order.getFinalPrice()); //즉, 사용한 마일리지 반환

        // 주문 취소 시, 해당 주문의 총 결제 금액 차감
        user.updateRecentTotal(-order.getTotalPrice());
    }


    @Scheduled(fixedRate = 60000) //테스트를 위해 바꿔놓음
    @Transactional
    public void updateOrderStatus() {

        // PROCESSING 상태면서 1분 이전에 생성된 주문 찾기
        List<Order> orders = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatus.PROCESSING,
                LocalDateTime.now().minusMinutes(1)
        );

        // 주문 상태를 'COMPLETE' 로 변경
        for (Order order : orders) {
            order.updateStatus(OrderStatus.COMPLETE);
        }
    }

}
