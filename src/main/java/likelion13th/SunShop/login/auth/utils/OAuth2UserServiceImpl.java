package likelion13th.SunShop.login.auth.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User src = super.loadUser(userRequest);
        Map<String, Object> attrs = new HashMap<>(src.getAttributes());

        String providerId = String.valueOf(attrs.get("id")); // Kakao 고유 ID
        String nickname = resolveNickname(attrs, providerId, 50);

        attrs.put("provider_id", providerId);
        attrs.put("nickname", nickname);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attrs,
                "provider_id" // getName()이 provider_id 반환
        );
    }

    @SuppressWarnings("unchecked")
    private static String resolveNickname(Map<String, Object> attrs, String providerId, int max) {
        Map<String, Object> acc = (Map<String, Object>) attrs.get("kakao_account");
        if (acc != null) {
            Map<String, Object> profile = (Map<String, Object>) acc.get("profile");
            if (profile != null) {
                Object n = profile.get("nickname");
                if (n instanceof String s && !s.isBlank()) return s.length() > max ? s.substring(0, max) : s;
            }
        }
        Map<String, Object> props = (Map<String, Object>) attrs.get("properties");
        if (props != null) {
            Object n = props.get("nickname");
            if (n instanceof String s && !s.isBlank()) return s.length() > max ? s.substring(0, max) : s;
        }
        String fb = "kakao_" + providerId;
        return fb.length() > max ? fb.substring(0, max) : fb;
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