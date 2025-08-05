package likelion13th.SunShop.global.api;

//응답 코드 인터페이스

// 모든 응답 코드가 구현해야 하는 규칙
public interface BaseCode {

    // ReasonDTO -> 응답 코드, 메시지, 상태 정보
    ReasonDto getReason();
}
