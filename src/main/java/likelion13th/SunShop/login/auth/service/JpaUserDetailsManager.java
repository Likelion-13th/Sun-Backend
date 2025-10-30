package likelion13th.SunShop.login.auth.service;

import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.login.auth.jwt.CustomUserDetails;
import likelion13th.SunShop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // // (선택) Spring 표준 예외
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

/**
 * // JpaUserDetailsManager
 * // - Spring Security의 UserDetailsManager 인터페이스를 JPA 기반으로 구현
 * // - 소셜 로그인(카카오)에서 providerId를 username으로 사용
 * // - 신규 사용자 생성(createUser), 존재 여부(userExists) 확인에 집중
 */
@Slf4j
@Service
public class JpaUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;   // // 사용자 조회/저장용 JPA 레포지토리
    private final PasswordEncoder passwordEncoder; // // (참고) 현재 소셜 로그인이라 직접 사용하지 않지만, 비밀번호 기반 로그인으로 확장 시 필요

    // // 생성자 주입 (스프링이 자동으로 빈 주입)
    public JpaUserDetailsManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * // Security가 인증 과정에서 호출하는 메서드
     * // - providerId를 username으로 간주하고 사용자 정보를 조회
     * // - 찾은 User 엔티티를 CustomUserDetails로 변환하여 반환
     */
    @Override
    public UserDetails loadUserByUsername(String providerId) throws UsernameNotFoundException {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> {
                    // // 없는 사용자면 우리 공통 예외로 변환 (API 응답 표준화에 맞춤)
                    log.warn("// 유저 정보 없음 (provider_id): {}", providerId);
                    throw new GeneralException(ErrorCode.USER_NOT_FOUND);
                });
        // // DB 엔티티 → Security에서 쓰는 UserDetails로 변환
        return CustomUserDetails.fromEntity(user);
    }
    // // 권장 형태(참고): 한 줄로 예외 공급
    // // .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

    /**
     * // 사용자 생성
     * // - 소셜 로그인 최초 성공 시 '신규 가입' 처리에서 호출됨
     * // - 여기서는 CustomUserDetails를 받아서 User 엔티티로 저장
     */
    @Override
    public void createUser(UserDetails user) {
        log.info("// 사용자 생성 시도 중 (provider_id): {}", user.getUsername());

        // // providerId(provider_id)가 이미 존재하면 생성 불가
        if (userExists(user.getUsername())) {
            log.warn("// 이미 존재하는 사용자 (provider_id): {}", user.getUsername());
            // // 메시지/코드 정책에 맞는 ErrorCode 사용 (필요 시 별도 USER_ALREADY_EXISTS 등 정의 권장)
            throw new GeneralException(ErrorCode.ALREADY_USED_NICKNAME);
        }

        try {
            // // CustomUserDetails → User 엔티티 변환 후 저장
            User newUser = ((CustomUserDetails) user).toEntity();

            // // (참고) 비밀번호 기반 사용자라면 여기서 passwordEncoder.encode(...) 필요
            // // newUser.setPassword(passwordEncoder.encode(rawPassword));

            userRepository.save(newUser);
            log.info("// 사용자 생성 완료 (provider_id): {}", user.getUsername());
        } catch (ClassCastException e) {
            // // 예상 타입이 아니면 내부 서버 에러로 래핑
            log.error("// UserDetails → CustomUserDetails 변환 실패 (provider_id): {}", user.getUsername(), e);
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * // 사용자 존재 여부 확인
     * // - providerId 기준으로 존재하는지 빠르게 체크
     */
    @Override
    public boolean userExists(String providerId) {
        log.info("사용자 존재 여부 확인 (provider_id): {}", providerId);
        return userRepository.existsByProviderId(providerId);
    }

    /**
     * // 사용자 정보 업데이트 (현재 미구현)
     * // - 소셜 로그인 시 서버에서 직접 갱신할 데이터 범위가 명확해진 뒤 구현 권장
     */
    @Override
    public void updateUser(UserDetails user) {
        log.error("사용자 정보 업데이트는 지원되지 않음 (provider_id): {}", user.getUsername());
        throw new UnsupportedOperationException("사용자 업데이트 기능은 아직 지원되지 않습니다.");
    }

    /**
     * // 사용자 삭제 (현재 미구현)
     * // - 실제 삭제 대신 '탈퇴 플래그'로 관리하는 소프트 삭제 전략을 권장
     */
    @Override
    public void deleteUser(String providerId) {
        log.error("사용자 삭제는 지원되지 않음 (provider_id): {}", providerId);
        throw new UnsupportedOperationException("사용자 삭제 기능은 아직 지원되지 않습니다.");
    }

    /**
     * // 비밀번호 변경 (소셜 로그인은 비밀번호를 사용하지 않음)
     * // - 자체 회원 가입/로그인 기능을 추가할 때 구현
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        log.error("비밀번호 변경은 지원되지 않음.");
        throw new UnsupportedOperationException("비밀번호 변경 기능은 아직 지원되지 않습니다.");
    }
}


//Spring Security는 인증 처리 시 내부적으로 UserDetailsService (혹은 UserDetailsManager)를 통해 사용자 정보를 로드함.
//Kakao OAuth 로그인으로 들어온 사용자도 도메인 User → CustomUserDetails로 변환해 주어야 시큐리티 필터 체인에서 인증 객체로 쓸 수 있음

/*
1) 왜 필요한가?
- Spring Security는 인증 과정에서 UserDetailsService나 UserDetailsManager를 통해 사용자 정보를 로드하는데
기본 구현은 메모리에 사용자 정보를 보관하기 때문에, 실제 DB와 연동하려면 커스텀 구현이 필요하다.
- JpaUserDetailsManager는 JPA(UserRepository)를 통해 DB에서 User를 조회 및 저장하고,
  CustomUserDetails로 감싸 SecurityContext가 이해할 수 있는 형태로 변환한다.

2) 없으면·틀리면?
SecurityContext에서 providerId로 사용자를 찾지 못해 로그인 이후 인증이 실패한다.
외부 로그인(OAuth2) 성공 후에도 DB 조회,저장이 안돼 서비스에서 사용자를 관리할 수 없다.
- createUser에서 중복 검사(userExists)가 누락되면 providerId 중복 가입 허용돼 데이터 무결성이 깨진다.
- UserDetails에서 CustomUserDetails 캐스팅 오류 시 DB 저장이 불가하다.

3) 핵심 설계 포인트
- 사용자 로딩(loadUserByUsername.
providerId를 username으로 사용한다. DB User에서 CustomUserDetails로 변환 후 SecurityContext에 올린다.)

- 사용자 생성(createUser.
신규 사용자 등록 시  providerId 중복 검사는 필수이다. CustomUserDetails를 엔티티로 변환 후 DB에 저장한다.)

- 사용자 존재 여부(userExists.
DB 레벨에서 providerId 존재 여부를 확인한다.)
 */