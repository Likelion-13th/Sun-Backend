package likelion13th.SunShop.login.auth.repository;

import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.login.auth.jwt.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RefreshToken 저장소
 * - 사용자(User)와 1:1로 매핑된 RefreshToken을 조회/삭제한다.
 * - Spring Data JPA의 파생 쿼리와 @Query(JPQL)를 혼용.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 사용자 엔티티로 RefreshToken 한 건을 조회
    // - 존재하지 않을 수 있으므로 Optional로 감싼다.
    Optional<RefreshToken> findByUser(User user);

    // 사용자 기준으로 RefreshToken을 삭제 (JPQL 직접 정의)
    // - @Modifying: DML(DELETE/UPDATE) 쿼리임을 명시
    // - 트랜잭션 경계(@Transactional)는 서비스 레이어에서 감싸는 것을 권장
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);
}


/*
1) 왜 필요한가?
- JWT 인증 구조에서 Refresh Token은 db에 저장,관리해야만 재발급,로그아웃,강제 만료가 가능기 때문에
서버는 사용자와 1:1 매핑된 RefreshToken을 저장해두고 토큰 재발급, 삭제, 특정 토큰 폐기 등과 같은 동작을 수행하며
이때 Spring Data JPA 기반 Repository가 필요하다.
(클라이언트에 Refresh Token만 주면 탈취 시 서버가 무효화할 방법이 없음)

2) 없으면·틀리면?
Refresh Token을 DB에서 조회,삭제할 수 없어 강제 로그아웃이나 재발급 제어가 불가하며,
Refresh Token 탈취에 대응할 방법이 없기 때문에 보안에 취약하다.

3) 핵심 설계 포인트
- 사용자 기준 조회(User 엔티티 객체를 그대로 파라미터로 받아 1:1 매핑된 Refresh Token 조회)
Optional<RefreshToken> findByUser(User user);

- 사용자 기준 삭제(user 기준으로 해당 RefreshToken 행 삭제)
@Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);
 */