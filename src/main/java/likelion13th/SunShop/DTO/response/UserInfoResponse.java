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
