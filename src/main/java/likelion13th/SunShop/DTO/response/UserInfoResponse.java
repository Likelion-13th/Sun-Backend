package likelion13th.SunShop.DTO.response;

import likelion13th.SunShop.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String usernickname;


    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getUsernickname()
        );
    }

}

// orderResponse 패턴과 동일하게 적용
// 클라이언트에 사용자 정보를 응답할 때 사용

