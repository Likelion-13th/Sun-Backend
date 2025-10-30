package likelion13th.SunShop.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMileageResponse {
    private int maxMileage;
}

// orderResponse 패턴과 동일하게 적용
//사용자 마일리지 및 누적 결제 금액을 클라이언트에 응답할 때 사용

