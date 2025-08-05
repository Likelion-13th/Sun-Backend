package likelion13th.SunShop.service;

import jakarta.transaction.Transactional;
import likelion13th.SunShop.DTO.request.AddressRequest;
import likelion13th.SunShop.DTO.response.AddressResponse;
import likelion13th.SunShop.domain.Address;
import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.login.auth.jwt.CustomUserDetails;
import likelion13th.SunShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAddressService {
    private final UserRepository userRepository;

    /** 사용자 주소 조회 **/
    @Transactional
    public AddressResponse getAddress(CustomUserDetails userDetails) {
        User user = userRepository.findByProviderId(userDetails.getProviderId())
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        return AddressResponse.from(user.getAddress());
    }

    /** 사용자 주소 저장/수정 **/
    @Transactional
    public void updateAddress(CustomUserDetails userDetails, AddressRequest request) {
        User user = userRepository.findByProviderId(userDetails.getProviderId())
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        Address newAddress = new Address(
                request.getZipcode(),
                request.getAddress(),
                request.getAddressDetail()
        );

        user.updateAddress(newAddress);
    }
}

//사용자 정보를 조회하고, 주소를 조회하거나 수정하는 기능
//User 엔티티의 updateAddress() 메서드로 도메인 객체 스스로 상태를 변경가능.
//응답/요청 DTO를 사용하여 계층 간 역할을 명확히 분리
