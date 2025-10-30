package likelion13th.SunShop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@Getter
public class Address {

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String address;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    public Address() {
        this.zipcode = "10540";
        this.address = "경기도 고양시 덕양구 항공대학로 76";
        this.addressDetail = "한국항공대학교";
    }
}
// 사용자의 주소 정보를 담당
// User 엔티티에 포함되어 별도 테이블 없이 컬럼으로 저장됨.