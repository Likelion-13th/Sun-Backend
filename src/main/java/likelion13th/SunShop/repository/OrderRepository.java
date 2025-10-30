package likelion13th.SunShop.repository;

import likelion13th.SunShop.domain.Order;
import likelion13th.SunShop.global.constant.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime dateTime);
}

//특정 상태이면서 특정 날짜 이전에 만들어진 주문들