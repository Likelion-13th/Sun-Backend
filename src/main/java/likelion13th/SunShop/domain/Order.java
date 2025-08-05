package likelion13th.SunShop.domain;

//주문 엔티티
import jakarta.persistence.*;
import likelion13th.SunShop.domain.entity.BaseEntity;
import likelion13th.SunShop.global.constant.OrderStatus;
import likelion13th.SunShop.login.auth.jwt.CustomUserDetails;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "orders") //예약어 회피
@NoArgsConstructor
public class Order extends BaseEntity {

    /** 필드 **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Setter
    @Column(nullable = false)
    private int totalPrice; // 기존 주문 내역을 유지하기 위해

    @Setter
    @Column(nullable = false)
    private int finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /** 연관관계 설정 **/
    // Item와의 관계 N:1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;

    // User와의 관계 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** 생성자 및 비즈니스 로직 등등..**/
    // 내부 생성자 메서드
    private Order(User user, Item item, int quantity) {
        this.user = user;
        this.item = item;
        this.quantity = quantity;
        this.status = OrderStatus.PROCESSING;
    }

    // 정적 팩토리 메서드
    public static Order create(CustomUserDetails customUserDetails, Item item, int quantity, int totalPrice, int finalPrice) {
        User user = customUserDetails.getUser();

        Order order = new Order(user, item, quantity);
        order.totalPrice = totalPrice;
        order.finalPrice = finalPrice;
        return order;
    }

    // 주문 상태 업데이트
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    //양방향 편의 메서드
    @SuppressWarnings("lombok")
    public void setUser(User user) {
        this.user = user;
    }
}


// 주문 정보를 관리. User와 Item과 연관되어 있음