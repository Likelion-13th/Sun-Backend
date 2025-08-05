package likelion13th.SunShop.global.config;

/*
import likelion13th.SunShop.login.auth.jwt.AuthCreationFilter;
import likelion13th.SunShop.login.auth.jwt.JwtValidationFilter;
import likelion13th.SunShop.login.auth.utils.OAuth2SuccessHandler;
import likelion13th.SunShop.login.auth.utils.OAuth2UserServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
*/

//보안 설정
/*
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthCreationFilter authCreationFilter;        // JWT 생성 필터
    private final JwtValidationFilter jwtValidationFilter;      // JWT 검증 필터
    private final OAuth2UserServiceImpl oAuth2UserService;      // 카카오 로그인 서비스
    private final OAuth2SuccessHandler oAuth2SuccessHandler;    // 로그인 성공 처리

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (API 서버이므로)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 (프론트엔드와 통신 허용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 접근 권한 설정 - 핵심!
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/health",                    // 헬스체크 - 누구나 접근 가능
                                "/swagger-ui/**",             // API 문서 - 개발용
                                "/v3/api-docs/**",
                                "/users/reissue",             // 토큰 재발급
                                "/users/logout",              // 로그아웃
                                "/oauth2/**",                 // 카카오 OAuth
                                "/login/oauth2/**",
                                "/categories/**",             // 카테고리 조회 - 로그인 없이 가능
                                "/items/**"                   // 상품 조회 - 로그인 없이 가능
                        ).permitAll()                     // ← 위 경로들은 인증 없이 접근 가능
                        .anyRequest().authenticated()     // ← 나머지는 모두 JWT 토큰 필요. ex) 주문 생성 요청
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // 관리자만 접근 가능
                        .requestMatchers("/seller/**").hasAnyRole("ADMIN", "SELLER")  // 관리자, 판매자 접근 가능
                )

                // 세션 사용 안함 (JWT 기반)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 카카오 OAuth2 로그인 설정 (UserService 연동)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)     // 로그인 성공 시 처리
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))      // 사용자 정보 처리
                )

                // JWT 필터 체인 설정
                .addFilterBefore(authCreationFilter, AnonymousAuthenticationFilter.class)
                .addFilterBefore(jwtValidationFilter, AuthCreationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 프론트엔드 도메인들
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",                              // 로컬 개발
                "http://sajang-dev.ap-northeast-2.elasticbeanstalk.com",  // 개발 서버
                "https://likelionshop.netlify.app"                    // 운영 서버
        ));

        // 허용할 HTTP 메서드들
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 헤더들 (JWT 토큰 포함)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // 쿠키 등 자격 증명 포함 여부
        configuration.setAllowCredentials(true);

        // 프리플라이트 요청 캐시 시간
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 적용
        return source;
    }


}
 */
