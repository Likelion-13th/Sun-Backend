package likelion13th.SunShop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion13th.SunShop.DTO.request.AddressRequest;
import likelion13th.SunShop.DTO.response.AddressResponse;
import likelion13th.SunShop.DTO.response.UserInfoResponse;
import likelion13th.SunShop.DTO.response.UserMileageResponse;
import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.global.api.ApiResponse;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.api.SuccessCode;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.login.auth.jwt.CustomUserDetails;
import likelion13th.SunShop.login.service.UserService;
import likelion13th.SunShop.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** 사용자 정보 조회, 주소 저장, 사용 가능 마일리지 조회 **/
@Tag(name = "회원 정보", description = "회원 정보 관련 API 입니다.")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserService userService;
    private final UserAddressService userAddressService;

    /** 사용자 정보 조회 **/
    @GetMapping("/profile")
    @Operation(summary = "사용자 정보 조회", description = "로그인한 사용자의 정보와 주문 상태별 개수를 조회합니다.")
    public ApiResponse<?> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.getAuthenticatedUser(customUserDetails.getProviderId());

        UserInfoResponse userInfo = UserInfoResponse.from(user);

        return ApiResponse.onSuccess(SuccessCode.USER_INFO_GET_SUCCESS, userInfo);
    }

    /** 주소 저장 **/
    @PostMapping("/address")
    @Operation(summary = "주소 저장", description = "로그인한 사용자의 주소를 저장합니다.")
    public ApiResponse<AddressResponse> saveAddress(
            @RequestBody AddressRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        AddressResponse addressResponse = userAddressService.saveAddress(customUserDetails.getProviderId(), request);
        return ApiResponse.onSuccess(SuccessCode.ADDRESS_SAVE_SUCCESS, addressResponse);
    }

    /** 로그인한 사용자의 사용 가능 마일리지 조회 **/
    // 결제창에서 쉽게 띄울 수 있도록 별도로 api 만듦
    @GetMapping("/mileage")
    @Operation(summary = "사용 가능 마일리지 조회", description = "로그인한 사용자의 사용 가능 마일리지를 조회합니다.")
    public ApiResponse<UserMileageResponse> getAvailableMileage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        // 로그인한 사용자 정보 조회
        User user = userService.getAuthenticatedUser(customUserDetails.getProviderId());
        // 사용 가능한 마일리지 반환
        return ApiResponse.onSuccess(SuccessCode.USER_MILEAGE_SUCCESS, new UserMileageResponse(user.getMaxMileage()));
    }
}

//사용자 ID를 기반으로 기본 정보를 조회한다.
//Swagger 문서화(@Operation)와 RESTful URL 설계(/users/info/{userId})를 적용
