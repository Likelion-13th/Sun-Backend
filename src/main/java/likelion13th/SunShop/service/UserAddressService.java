package likelion13th.SunShop.service;

import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.domain.Address;
import likelion13th.SunShop.DTO.request.AddressRequest;
import likelion13th.SunShop.DTO.response.AddressResponse;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.exception.CustomException;
import likelion13th.SunShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserRepository userRepository;

    // 사용자 주소 저장 (기본값 또는 변경)
    @Transactional
    public AddressResponse saveAddress(String providerId, AddressRequest request) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자가 입력한 값이 없을 경우 기본 주소 사용
        String zipcode = request.getZipcode();
        String address = request.getAddress();
        String detail = request.getAddressDetail();

        // 새로운 주소 설정
        Address newAddress = new Address(zipcode, address, detail);
        user.updateAddress(newAddress); // User 엔티티에 주소 업데이트
        userRepository.save(user); // 변경 사항 저장

        return new AddressResponse(user.getAddress());
    }

    // 사용자 주소 조회 (기본값 -> 항공대로 제공)
    @Transactional(readOnly = true)
    public AddressResponse getAddress(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new AddressResponse(user.getAddress());
    }
}

//사용자 정보를 조회하고, 주소를 조회하거나 수정하는 기능
//User 엔티티의 updateAddress() 메서드로 도메인 객체 스스로 상태를 변경가능.
//응답/요청 DTO를 사용하여 계층 간 역할을 명확히 분리
