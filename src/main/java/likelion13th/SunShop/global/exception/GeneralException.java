package likelion13th.SunShop.global.exception;

import likelion13th.SunShop.global.api.BaseCode;
import likelion13th.SunShop.global.api.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

//일반 예외

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private final BaseCode code;

    //예외 생성
    public static GeneralException of(BaseCode code) {
        return new GeneralException(code);
    }

    //예외 상세 정보
    public ReasonDto getReason() {
        return this.code.getReason();
    }
}
