package likelion13th.SunShop.DTO.response;

import likelion13th.SunShop.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMileageResponse {
    private int maxMileage;
    private int recentTotal;

    public static UserMileageResponse from(User user) {
        return new UserMileageResponse(
                user.getMaxMileage(),
                user.getRecentTotal()
        );
    }
}
