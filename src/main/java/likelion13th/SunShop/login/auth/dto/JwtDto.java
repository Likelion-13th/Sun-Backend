package likelion13th.SunShop.login.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class JwtDto {
    private String accessToken;
    private String refreshToken;

    //주고 받기 때문에 public으로 선언.
    public JwtDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

//(액세스 토큰, 리프레시 토큰, 만료 시간 등)을 일관된 자료형으로 묶어서 반환-> 응답 일관성, 확장성

/*
1) 왜 필요한가
JWT 기반 인증에서 클라이언트에게 전하는 토큰들을 따로 반환하게 되면 유지보수, 관리가 힘듦
DTO로 묶어서 응답하면 일관된 자료형으로 처리 가능.

2) 없으면/틀리면?
 토큰을 String이나 Map으로 넘겨주면 컨트롤러, 서비스, 응답 JSON이 불일치 -> 혼동 가능.
 발급 시각이나 사용자 식별 추가 등 확장 시 기존 코드 전반을 수정해야 할 위험이 있다.

3) 핵심 설계 포인트
DTO 패턴: 인증 관련된 반환 데이터를 하나의 객체로 묶어서 전달한다.
-> JwtDto(String accessToken, String refreshToken)
 */
