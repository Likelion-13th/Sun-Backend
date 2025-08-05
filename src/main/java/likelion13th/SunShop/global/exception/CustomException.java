package likelion13th.SunShop.global.exception;

import likelion13th.SunShop.global.api.BaseCode;
import lombok.Getter;

//커스텀 예외

@Getter
public class CustomException extends RuntimeException {
    private final BaseCode errorCode;

    public CustomException(BaseCode errorCode) {
        super(errorCode.getReason().getMessage());
        this.errorCode = errorCode;
    }
}
