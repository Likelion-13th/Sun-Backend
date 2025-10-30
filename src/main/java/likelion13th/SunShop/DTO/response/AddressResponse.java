package likelion13th.SunShop.DTO.response;

import likelion13th.SunShop.domain.Address;
import lombok.Getter;

@Getter
public class AddressResponse {
    private String zipcode;
    private String address;
    private String addressDetail;

    public AddressResponse(Address address) {
        this.zipcode = address.getZipcode();
        this.address = address.getAddress();
        this.addressDetail = address.getAddressDetail();
    }
}