package likelion13th.SunShop.DTO.response;

import likelion13th.SunShop.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressResponse {
    private String zipcode;
    private String address;
    private String addressDetail;

    public static AddressResponse from(Address address) {
        return new AddressResponse(
                address.getZipcode(),
                address.getAddress(),
                address.getAddressDetail()
        );
    }
}
