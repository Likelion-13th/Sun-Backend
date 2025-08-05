package likelion13th.SunShop.global.exception;

import likelion13th.SunShop.global.api.BaseCode;

//파일 업로드 관련 예외

public class FileUploadException extends CustomException {
    public FileUploadException(BaseCode errorCode) {
        super(errorCode);
    }
}
