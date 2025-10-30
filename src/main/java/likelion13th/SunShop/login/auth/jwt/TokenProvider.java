package likelion13th.SunShop.login.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.login.auth.dto.JwtDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * TokenProvider
 * - JWT(Access/Refresh) 생성·검증·파싱을 담당
 *
 * 토큰 설계:
 * - subject(sub): providerId(카카오 고유 ID)를 저장
 * - iat(발급시각), exp(만료시각) 기본 포함
 * - authorities(문자열): Access Token에만 포함, Refresh Token에는 포함하지 않음
 *
 * 서명 알고리즘:
 * - HS256 (대칭키) 사용
 * - secretKey는 32바이트(256비트) 이상이 권장됨 (jjwt Keys.hmacShaKeyFor 요구사항)
 *   예: 환경변수에 충분히 긴 랜덤 바이트를 Base64로 넣어 사용
 */
@Slf4j
@Component
public class TokenProvider {

    private final Key secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    /**
     * 생성자: 설정(application.yml / 환경변수)에서 키와 만료 시간 주입
     *
     * @param secretKey              HS256 서명용 시크릿 (32바이트 이상 권장)
     * @param accessTokenExpiration  Access Token 만료(ms)
     * @param refreshTokenExpiration Refresh Token 만료(ms)
     *
     * 예시(yml):
     *   JWT_SECRET: ${JWT_SECRET}
     *   JWT_EXPIRATION: 900000        # 15분
     *   JWT_REFRESH_EXPIRATION: 1209600000 # 14일
     */
    public TokenProvider(
            @Value("${JWT_SECRET}") String secretKey,
            @Value("${JWT_EXPIRATION}") long accessTokenExpiration,
            @Value("${JWT_REFRESH_EXPIRATION}") long refreshTokenExpiration) {
        // jjwt가 내부적으로 키 길이를 검사하므로, 충분히 긴 바이트 배열이어야 함
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * AccessToken 및 RefreshToken 동시 생성
     * - UserDetails에서 providerId(username)와 권한을 읽어 AccessToken에만 권한을 claim으로 넣는다.
     */
    public JwtDto generateTokens(UserDetails userDetails) {
        log.info("JWT 생성 시작: 사용자 {}", userDetails.getUsername());

        // username == providerId (우리 서비스 정책)
        String userId = userDetails.getUsername();

        // 권한 목록을 "ROLE_USER,ROLE_ADMIN" 형태의 문자열로 직렬화 (AccessToken에만 저장)
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Access Token 생성 (권한 포함)
        String accessToken = createToken(userId, authorities, accessTokenExpiration);

        // Refresh Token 생성 (권한 불포함: 재발급 용도로만 사용)
        String refreshToken = createToken(userId, null, refreshTokenExpiration);

        log.info("Access/Refresh 토큰 생성 완료 (userId: {})", userId);
        return new JwtDto(accessToken, refreshToken);
    }

    /**
     * 공통 JWT 생성 로직
     *
     * @param providerId     사용자 식별자(= provider_id) → JWT의 subject로 저장
     * @param authorities    권한 문자열 (Access 전용, 예: "ROLE_USER,ROLE_ADMIN")
     * @param expirationTime 만료 시간(ms)
     */
    private String createToken(String providerId, String authorities, long expirationTime) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(providerId)                                   // sub
                .setIssuedAt(new Date())                                  // iat
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // exp
                .signWith(secretKey, SignatureAlgorithm.HS256);           // 서명

        // Access Token에만 권한을 실어 보낸다.
        if (authorities != null) {
            jwtBuilder.claim("authorities", authorities);
        }

        return jwtBuilder.compact();
    }

    /**
     * 토큰 유효성 검증
     * - 서명/구조/만료 등을 검증하고, 예외가 없으면 유효한 것으로 간주
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token); // 파싱 성공 == 유효
            return true;
        } catch (JwtException e) {
            // SignatureException, MalformedJwtException, ExpiredJwtException 등 모두 포함
            log.warn("JWT 검증 실패: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    /**
     * 토큰에서 Claims 추출
     * - 만료(ExpiredJwtException) 시 예외를 상위로 던져 호출부에서 별도 처리
     * - 그 외 파싱 실패는 TOKEN_INVALID로 래핑
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료: {}", e.getClass().getSimpleName());
            throw e; // 재발급 플로우 등 호출부에서 만료를 구분 처리
        } catch (JwtException e) {
            log.warn("JWT 파싱 실패: {}", e.getClass().getSimpleName());
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * Claims → 권한 정보 복원
     * - "ROLE_USER,ROLE_ADMIN" 문자열을 SimpleGrantedAuthority 리스트로 변환
     * - 권한이 없으면 기본 ROLE_USER 부여 (게스트 최소 권한)
     */
    public Collection<? extends GrantedAuthority> getAuthFromClaims(Claims claims) {
        String authoritiesString = claims.get("authorities", String.class);
        if (authoritiesString == null || authoritiesString.isEmpty()) {
            log.warn("권한 정보 없음 - 기본 ROLE_USER 부여");
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Arrays.stream(authoritiesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * 만료 허용 파싱
     * - 만료된 토큰이라도 Claims만 뽑아 써야 하는 경우(예: Refresh로 재발급) 사용
     */
    public Claims parseClaimsAllowExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 만료됐지만 payload(Claims)는 안전하게 획득 가능
            return e.getClaims();
        }
    }
}

/*
 * [실무 팁]
 * 1) 토큰/시크릿키를 절대 로그로 직접 출력하지 않기 (특히 Production)
 * 2) 전송은 반드시 HTTPS로만 (중간자 공격 방지)
 * 3) 키 순환(Key Rotation)을 고려: kid 헤더 도입 → 키 변경 시점에 대비
 * 4) Clock Skew(서버-클라이언트 시계 차) 허용이 필요하면 parserBuilder().setAllowedClockSkewSeconds(...) 활용
 * 5) Refresh Token 저장 전략:
 *    - DB/Redis 등에 블랙리스트/화이트리스트로 관리하거나, 최신 토큰만 유효하도록 설계
 * 6) 쿠키로 전달할 때는 HttpOnly + Secure + SameSite 옵션을 반드시 설정
 */


//JWT 토큰을 생성, 검증, 파싱하는 로직을 모아두는 유틸/서비스 클래스
// -> "토큰 관리 표준화"를 위해 **권한(authorities), 만료 처리(expiration), 서명 검증(validation)**을 모두 책임

/*
1) 왜 필요한가?
- JWT 기반 인증 시스템에서 핵심은 토큰 발급, 검증, 파싱인데 이것은 Spring Security의 AuthenticationManager/Filter와
직접 연결되어 Access, Refresh Token을 생성하고 해석해야 한다. 분산 환경에서는 세션이 없으므로 서버는
토큰 자체를 신뢰해야 하며, 이떄 표준화된 TokenProvider가 각 계층에서 JWT 처리 로직을 통합해 유지보수와 보안을 강화시킨다.
-> 토큰 관리(생성, 만료, 권한 추출 등)를 단일 책임 클래스로 묶은 것

2) 없으면·틀리면?
- 토큰 관련 로직이 컨트롤러나 서비스에 중복으로 구현되면 코드 중복으로 인한 오류가 발생한다.
- Access,Refresh 만료 정책이나 권한 Claim 규약이 불일치 한다.
- 틀리면:
    - scretKey가 일치하지 않으면 SignatureException으로 모든 토큰을 무효화시킨다.
    - expiration을 잘못 처리했을 때 만료 토큰을 계속 받아들이거나 유효 토큰을 거부할 수 있다.
    - authorities Claim을 잘못 처리 했을 때 ROLE 부여 누락으로 Authorization가 실패한다.

3) 핵심 설계 포인트
- application yml, properties에서 비밀키,만료시간을 관리
public TokenProvider (
            @Value("${JWT_SECRET") String secretKey,

- Access,Refresh Token 동시 발급으로 API 응답 일관성 유지
public JwtDto generateTokens(UserDetails userDetails)

- 토큰 생성 로직 캡슐화
private String createToken(String providerId, String authorities, long expirationTime)

- 검증(서명 불일치, 만료 등 JwtException 발생 시 false)
public boolean validateToken(String toeken)

-Claims 파싱(유효 토큰은 Claim 반환, 만료 시 ExpiredJwtException으로 throw)
public Claims parseClaims(String token)

-권한 복원 (authorities Claim 문자열을 ','로 split 후 ROLE 목록 복원, 없거나 빈 경우 ROLE_USER 기본값 부여)
public Collection<? extends GrantedAuthority> getAuthFromClaims(Claims claims)
 */