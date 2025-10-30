package likelion13th.SunShop.DTO.response;

import likelion13th.SunShop.domain.Order;
import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.global.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private String usernickname;
    private int recentTotal;
    private int maxMileage;
    private Map<OrderStatus, Integer> orderStatusCounts; // 각 상태별 주문 개수

    public static UserInfoResponse from(User user) {
        // 각 상태별 주문 개수 계산
        Map<OrderStatus, Integer> orderStatusCounts = user.getOrders().stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // PROCESSING, COMPLETE, CANCEL 상태가 없는 경우 0으로 초기화
        orderStatusCounts.putIfAbsent(OrderStatus.PROCESSING, 0);
        orderStatusCounts.putIfAbsent(OrderStatus.COMPLETE, 0);
        orderStatusCounts.putIfAbsent(OrderStatus.CANCEL, 0);

        return new UserInfoResponse(
                user.getUsernickname(),
                user.getRecentTotal(),
                user.getMaxMileage(),
                orderStatusCounts
        );
    }
}

// orderResponse 패턴과 동일하게 적용
// 클라이언트에 사용자 정보를 응답할 때 사용

