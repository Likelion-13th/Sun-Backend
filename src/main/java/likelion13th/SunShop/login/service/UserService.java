package likelion13th.SunShop.login.service;

import jakarta.transaction.Transactional;
import likelion13th.SunShop.DTO.response.UserInfoResponse;
import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//로그인/인증 관련. 사용자 서비스
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getAuthenticatedUser(String providerId) {
        return userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public UserInfoResponse getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.from(user);
    }
}
