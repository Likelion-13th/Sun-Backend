package likelion13th.SunShop.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressRequest {
    private String zipcode;
    private String address;
    private String addressDetail;
}

// orderCreateRequest 패턴과 동일하게 적용
//사용자 주소 저장 및 수정 요청 시 클라이언트로부터 전달받는 데이터를 담는 요청 DTO