package likelion13th.SunShop.login.auth.utils;

import likelion13th.SunShop.domain.User;
import likelion13th.SunShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 카카오 OAuth2 사용자 정보를 조회하고, 우리 서비스에서 사용할 수 있는 형태로 가공하는 서비스
 * - Spring Security의 DefaultOAuth2UserService를 상속하여 loadUser()를 커스터마이징
 * - 카카오에서 내려주는 사용자 정보 중 provider_id(고유 ID), nickname을 추출하여 attributes에 추가
 * - Authentication 성공 시 SecurityContext에 저장될 OAuth2User를 반환
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    // 신규 회원 가입/조회 로직이 필요한 경우를 대비해 주입해 둠
    // 현재 코드에서는 사용하지 않지만, 추후 providerId 기반 회원 자동 생성 등에 활용 가능
    private final UserRepository userRepository;

    /**
     * 카카오에서 사용자 정보를 불러오고(원본 attributes),
     * 우리 서비스에서 쓰기 좋은 key(provider_id, nickname)를 추가해 확장 attributes로 반환한다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 1) 부모 클래스가 실제로 카카오 API를 호출하여 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("// 카카오 OAuth2 로그인 시도");

        // 2) 카카오가 내려주는 사용자 원본 attributes 예시
        //    {
        //      "id": 1234567890,
        //      "connected_at": "...",
        //      "properties": { "nickname": "홍길동", ... },
        //      "kakao_account": { ... }
        //    }

        // 2-1) 카카오 고유 식별자(ID) → 우리 서비스의 provider_id로 사용
        String providerId = oAuth2User.getAttributes().get("id").toString();

        // 2-2) 프로필 닉네임 추출 (없을 수 있으므로 getOrDefault 사용)
        //      카카오 attributes의 "properties"는 중첩 Map 구조이므로 캐스팅 필요
        @SuppressWarnings("unchecked")
        Map<String, Object> properties =
                (Map<String, Object>) oAuth2User.getAttributes().getOrDefault("properties", Collections.emptyMap());
        String nickname = properties.getOrDefault("nickname", "카카오사용자").toString();

        // 3) Security에서 사용할 attributes를 확장:
        //    - 원본 attributes를 복사한 뒤 provider_id, nickname을 명시적 key로 추가
        Map<String, Object> extendedAttributes = new HashMap<>(oAuth2User.getAttributes());
        extendedAttributes.put("provider_id", providerId);
        extendedAttributes.put("nickname", nickname);

        // 4) 최종 OAuth2User 생성하여 반환
        //    - 권한: 기본 USER 권한 부여 (필요 시 ROLE_ADMIN 등 추가 가능)
        //    - attributes: 위에서 확장한 extendedAttributes 사용
        //    - nameAttributeKey: Security에서 "사용자 이름"으로 인식할 키 → 여기서는 provider_id로 지정
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), // 부여할 권한 목록
                extendedAttributes,                                            // 사용자 속성(세션/인증 객체에 담김)
                "provider_id"                                                 // getName()이 반환할 키
        );
    }
}


// 카카오에서 받은 외부 데이터(JSON)를 우리 도메인(User)과 맞춰서 가공
//카카오에서 주는 providerId, nickname 같은 값들을 꺼내서
// → 우리 User 엔티티와 연결하고 → CustomUserDetails or OAuth2User로 변환

/*
1) 왜 필요한가?
- 소셜 로그인은 외부 제공자(카카오)가 반환하는 사용자 정보(JSON)가 제각각이므로
이 값을 우리 서비스(User Entity)에서 일관된 키(providerId, nickname 등)로 쓰려면 정규화단계가 필요하다.

2) 없으면·틀리면?
Spring Security가 provider가 주는 원본 attributes를 그대로 principal에 넣었을 때
getName()이 providerId가 아닌 다른 키가 돼 TokenProvider, UserDetails, DB 등의 인증이 실패한다.
- nameAttributeKey를 provider_id로 지정하지 않으면 getName() 값이 의도와 달라진다.

3) 핵심 설계 포인트
- 원본 Attributes 로드 (provider에서 내려준 JSON 파싱)
OAuth2User oAuth2User = super.loadUser(userRequest);

- 표준 키로 변환으로 이후 계층에서는 provider_id와 nickname 만 신뢰하면 됨.

- SecurityContext 주입 객체 생성(권한으로 최소 ROLE_USER 기본 부여,
nameAttributeKey를 provider_id로 고정시켜 TokenProvider와 CustomUserDetails가 동일한 username 사용.)
return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                extendedAttributes,
                "provider_id"
        );
 */