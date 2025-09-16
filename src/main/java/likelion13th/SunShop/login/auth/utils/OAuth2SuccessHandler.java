package likelion13th.SunShop.login.auth.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13th.SunShop.domain.Address;
import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.login.auth.dto.JwtDto;
import likelion13th.SunShop.login.auth.jwt.CustomUserDetails;
import likelion13th.SunShop.login.auth.service.JpaUserDetailsManager;
import likelion13th.SunShop.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JpaUserDetailsManager jpaUserDetailsManager;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        String providerId = (String) oAuth2User.getAttributes().get("provider_id");
        String nickname = (String) oAuth2User.getAttributes().get("nickname");

        String maskedPid  = (providerId != null && providerId.length() > 4) ? providerId.substring(0, 4) + "***" : "***";
        String maskedNick = (nickname != null && !nickname.isBlank()) ? "*(hidden)*" : "(none)";

        if (!jpaUserDetailsManager.userExists(providerId)) {
            User newUser = User.builder()
                    .providerId(providerId)
                    .usernickname(nickname)
                    .deletable(true)
                    .build();


        newUser.setAddress(new Address("10540", "경기도 고양시 덕양구 항공대학로 76", "한국항공대학교"));

        CustomUserDetails userDetails = new CustomUserDetails(newUser);
        jpaUserDetailsManager.createUser(userDetails);
        log.info("신규 회원 등록이용");
    } else {
        log.info("기존 회원 이용");
    }

    JwtDto jwt = userService.jwtMakeSave(providerId);
    log.info("JWT 발급 완료 - providerId(masked)={}",maskedPid);

    String frontendRedirectUri = request.getParameter("redirect_uri");
    List<String> authorizedUris = List.of(
            "https://sun-shop.netlify.app/",
            "http://localhost:3000"
    );
    if(frontendRedirectUri ==null||!authorizedUris.contains(frontendRedirectUri)) {
        frontendRedirectUri = "https://sun-shop.netlify.app/";
    }

    String redirectUrl = UriComponentsBuilder
            .fromUriString(frontendRedirectUri)
            .queryParam("accessToken", jwt.getAccessToken())
            .build()
            .toUriString();

    log.info("Redirecting to authorized frontend host: {}",frontendRedirectUri);

    response.sendRedirect(redirectUrl);
    }

}

//로그인 성공 시 후처리 담당.
/*
1) 왜 필요한가?
OAuth2 로그인은 외부 인증 성공만 처리하기 때문에 그 직후 신규 사용자 db 등록, 토큰 JWT 발급 및 저장,
토큰 리다이렉트 등의 후처리를 처리해준다.

2) 없으면·틀리면?
- 외부 로그인은 성공했지만, 실제 서비스 db에는 사용자가 없어 UserDetailsService,JpaUserDetailsManager가 이후 인증 과정에서 실패한다.
- 토큰이 발급되지 않아 프론트에서 API를 호출할 수 없다.
- 틀리면:
  - userExists 검사 로직 오류 시 신규 가입에서 같은 사용자가 매번 새로 등록됨.
  - providerId,nickname 매핑 오류: DB 식별자 불일치로 인한 중복 계정 생성.
  - JWT 발급 누락/실패로 프론트엔드가 토큰 없이 리다이렉트하게되면 로그인 무한 루프.

3) 핵심 설계 포인트
- 신규,기존 사용자 구분 (providerId 기준 중복 가입 방지)
 if (!jpaUserDetailsManager.userExists(providerId)) {
            User newUser = User.builder()
                    .providerId(providerId)
                    .usernickname(nickname)
                    .deletable(true)
                    .build();


        newUser.setAddress(new Address("10540", "경기도 고양시 덕양구 항공대학로 76", "한국항공대학교"));

        CustomUserDetails userDetails = new CustomUserDetails(newUser);
        jpaUserDetailsManager.createUser(userDetails);
        log.info("신규 회원 등록이용");
    } else {
        log.info("기존 회원 이용");
    }

- JWT 발급 및 저장 ( Access,Refresh Token 동시 발급. refresh 는 db, redis에 저장해 재발급 할 수 있게 한다)
JwtDto jwt = userService.jwtMakeSave(providerId);

- redirect_uri 안전 검증(프론트엔드 리다이렉트 대상 리스트화로 open redirect를 방지한다.)
String frontendRedirectUri = request.getParameter("redirect_uri");
    List<String> authorizedUris = List.of(
            "https://sun-shop.netlify.app/",
            "http://localhost:3000"
    );
    if(frontendRedirectUri ==null||!authorizedUris.contains(frontendRedirectUri)) {
        frontendRedirectUri = "https://sun-shop.netlify.app/";
    }

- 쿼리 파라미터로 토큰 전달
String redirectUrl = UriComponentsBuilder
            .fromUriString(frontendRedirectUri)
            .queryParam("accessToken", jwt.getAccessToken())
            .build()
            .toUriString();

    log.info("Redirecting to authorized frontend host: {}",frontendRedirectUri);

    response.sendRedirect(redirectUrl);
    }

 */