package likelion13th.SunShop.login.auth.jwt;

import likelion13th.SunShop.domain.Address;
import likelion13th.SunShop.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {
    private Long userId;
    private String providerId;
    private String usernickname;
    private Address address;

    private Collection<? extends GrantedAuthority> authorities;

    //생성자
    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.providerId = user.getProviderId();
        this.usernickname = user.getUsernickname();
        this.address = user.getAddress();
        //기본 권한을 가진 사용자로 반환
        this.authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    //비밀번호 설정, 권한 부여
    public CustomUserDetails(String providerId, String password, Collection<? extends GrantedAuthority> authorities) {
        this.providerId = providerId;
        this.userId = null;
        this.usernickname = null;
        this.authorities = authorities;
        this.address = null;
    }

    //User 엔티티 <- CustomUserDetails 변환
    public static CustomUserDetails fromEntity(User entity) {
        return CustomUserDetails.builder()
                .userId(entity.getId())
                .providerId(entity.getProviderId())
                .usernickname(entity.getUsernickname())
                .address(entity.getAddress())
                .build();

    }

    //User 엔티티 <- CustomUSerDetails 변환
    public User toEntity(){
        return User.builder()
                .id(this.userId)                //PK (신규 생성이면 null 허용)
                .providerId(this.providerId)        //소셜 고유 ID
                .usernickname(this.usernickname)    //닉네임
                .address(this.address)              //주소
                .build();
    }

    @Override
    public String getUsername() {
        return this.providerId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.authorities != null && !this.authorities.isEmpty()) {
            return this.authorities;
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override
    public String getPassword() {
        // 소셜 로그인은 비밀번호를 사용하지 않음
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 정책 사용 시 실제 값으로 교체
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 잠금 정책 사용 시 실제 값으로 교체

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명(비밀번호) 만료 정책 사용 시 실제 값으로 교체
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 활성/비활성 정책 사용 시 실제 값으로 교체 (예: 탈퇴/정지 사용자)
        return true;
    }
}

//도메인 정보(User) → 스프링 시큐리티에서 이해할 수 있는 인증 객체(UserDetails) 변환 브릿지 역할
/*
1) 왜 필요한가?
도메인 User 엔티티를 Spring Security가 이해하는 인증 주체로 변환하는 역할이다.
SecurityContext에는 User가 아닌 USerDetails가 들어가기 때문에 인증 이후 필터 -> 핸들러 -> 서비스 등
모든 영역에서 일관된 방식으로 사용자 정보를 참조할 수 있다.
카카오 로그인 에서는 providerId가 username 역할.
(getUsername()에서 providerId 반환하며 토큰생성/검증, 권한 확인, 로그 등의 식별자를 통일시킴)

2) 없으면/틀리면?
- SecurityContext에 적절한 값을 넣지 못해 이후 우리 도메인 정보(userid, 닉네임, 주소 등)에 안전하게 접근하기 어렵다.
- getUsername()!=providerId 이면 토큰 subject가 providerID가 아닐 수 있어 토큰의 주인 식별이 불가하다.
(신규 가입, 중복 가입, 권한 부여 로직에서 오류 발생 가능)
- ROLE_USER가 부여되지 않거나 실제 부여된 권한을 항상 ROLE_USER로 고정되는 문제 등이 발생할 수 있다.


3) 핵심 설계 포인트
- username = providerId로 고정해 모든 인증 경로의 단일한 식별자로 설정하였따.
public String getUsername() {
        return this.providerId;
    }

- 권한 컬렉션을 안전하게 반환

if(this.authorities != null && !this.authorities.isEmpty()) {
            return this.authorities;
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
 */