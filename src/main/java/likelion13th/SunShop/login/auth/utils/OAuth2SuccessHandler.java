package likelion13th.SunShop.login.auth.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.login.auth.dto.JwtDto;
import likelion13th.SunShop.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    private static final List<String> ALLOWED_ORIGINS = List.of(
            "https://sun-shop.netlify.app",
            "http://localhost:3000"
    );
    private static final String DEFAULT_FRONT_ORIGIN = "https://sun-shop.netlify.app";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            DefaultOAuth2User principal = (DefaultOAuth2User) ((OAuth2AuthenticationToken) authentication).getPrincipal();
            Map<String, Object> attrs = principal.getAttributes();

            String providerId = String.valueOf(attrs.getOrDefault("provider_id", attrs.get("id")));
            String nickname   = String.valueOf(attrs.getOrDefault("nickname", "kakao_" + providerId));
            log.info("[OAuth2Success] providerId={}, nickname={}", providerId, nickname);

            // ✅ 신규 가입 보장
            userService.ensureUserExists(providerId, nickname);

            // ✅ JWT 발급
            JwtDto jwt = userService.jwtMakeSave(providerId);

            String origin = (String) request.getSession().getAttribute("FRONT_REDIRECT_URI");
            request.getSession().removeAttribute("FRONT_REDIRECT_URI");
            if (origin == null || !ALLOWED_ORIGINS.contains(origin)) origin = DEFAULT_FRONT_ORIGIN;

            String redirectUrl = UriComponentsBuilder.fromUriString(origin)
                    .queryParam("accessToken", URLEncoder.encode(jwt.getAccessToken(), StandardCharsets.UTF_8))
                    .build(true).toUriString();

            response.sendRedirect(redirectUrl);

        } catch (GeneralException e) {
            log.error("[OAuth2Success] 도메인 예외: {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[OAuth2Success] 예기치 못한 오류: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

