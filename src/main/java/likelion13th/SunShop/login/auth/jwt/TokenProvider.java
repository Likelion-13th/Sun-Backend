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

@Slf4j
@Component
public class TokenProvider {
    private final Key secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public TokenProvider (
            @Value("${JWT_SECRET") String secretKey,
            @Value("${JWT_EXPIRATION") long accessTokenExpiration,
            @Value("${JWT_REFRESH_EXPIRATION") long refreshTokenExpiration){
                this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
                this.accessTokenExpiration = accessTokenExpiration;
                this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public JwtDto generateTokens(UserDetails userDetails){
        log.info("JWT 생성: 사용자 {}", userDetails.getUsername());

        String userId = userDetails.getUsername();

        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = createToken(userId, authorities, accessTokenExpiration);
        String refreshToken = createToken(userId, null, refreshTokenExpiration);

        log.info("JWT 생성 완료: 사용자 {}", userDetails.getUsername());
        return new JwtDto(accessToken, refreshToken);
    }

    private String createToken(String providerId, String authorities, long expirationTime) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(providerId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256);

        if(authorities != null) {
            builder.claim("authorities", authorities);
        }

        return builder.compact().toString();
    }

    public boolean validateToken(String toeken){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(toeken);
            return true;
        }
        catch (JwtException e){
            return false;
        }
    }

    public Claims parseClaims(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e){
            log.warn("토큰 만료");
            throw e;
        } catch(JwtException e){
            log.warn("JWT 파싱 실패");
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }
    }

    public Collection<? extends GrantedAuthority> getAuthFromClaims(Claims claims){
        String authoritiesString = claims.get("authorities", String.class);
        if (authoritiesString != null || authoritiesString.isEmpty()) {
            log.warn("권한 정보가 없다 - 기본 ROLE_USER 부여");
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Arrays.stream(authoritiesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Claims parseClaimsAllowExpired(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}

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