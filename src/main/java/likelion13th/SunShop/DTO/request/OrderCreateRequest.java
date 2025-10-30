package likelion13th.SunShop.DTO.request;

//데이터 전송 객체. 주문 생성 요청 DTO
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {
    private Long itemId;
    private int quantity;
    private int mileageToUse;
}