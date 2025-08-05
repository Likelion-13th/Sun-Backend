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
