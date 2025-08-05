package likelion13th.SunShop.controller; // 패키지 선언 - 컨트롤러 클래스들이 위치한 패키지

// 필요한 라이브러리들 import
import io.swagger.v3.oas.annotations.Operation; // Swagger API 문서화를 위한 어노테이션
import likelion13th.SunShop.DTO.response.ItemResponse; // 상품 응답 데이터 전송 객체
import likelion13th.SunShop.global.api.ApiResponse; // 통일된 API 응답 형식을 위한 클래스
import likelion13th.SunShop.global.api.ErrorCode; // 에러 코드 정의 enum
import likelion13th.SunShop.global.api.SuccessCode; // 성공 코드 정의 enum
import likelion13th.SunShop.service.ItemService; // 상품 관련 비즈니스 로직을 처리하는 서비스
import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 자동 생성하는 롬복 어노테이션
import org.springframework.web.bind.annotation.GetMapping; // HTTP GET 요청을 처리하는 어노테이션
import org.springframework.web.bind.annotation.PathVariable; // URL 경로 변수를 매개변수로 받는 어노테이션
import org.springframework.web.bind.annotation.RequestMapping; // 클래스 레벨에서 기본 URL 매핑을 설정하는 어노테이션
import org.springframework.web.bind.annotation.RestController; // REST API 컨트롤러임을 나타내는 어노테이션

import java.util.List;

@RestController // 이 클래스가 REST API 컨트롤러임을 Spring에 알림 (JSON 응답 자동 변환)
@RequestMapping("/items") // 이 컨트롤러의 모든 엔드포인트 앞에 "/items" 경로가 붙음
@RequiredArgsConstructor // final 필드인 itemService에 대한 생성자를 자동으로 생성 (의존성 주입용)
public class ItemController { // 상품 관련 HTTP 요청을 처리하는 컨트롤러 클래스

    private final ItemService itemService; // 상품 관련 비즈니스 로직을 처리할 서비스 객체 (final로 불변성 보장)

    // 개별 상품 조회 API
    @GetMapping("/{itemId}") // HTTP GET 요청을 "/items/{itemId}" 경로로 매핑
    @Operation(summary = "상품 개별 조회", description = "상품을 개별 조회합니다.") // Swagger 문서에 표시될 API 설명
    public ApiResponse<?> getItemById(@PathVariable Long itemId) { // URL의 {itemId} 부분을 Long 타입 매개변수로 받음

        // 서비스에서 해당 ID의 상품 정보를 조회
        ItemResponse item = itemService.getItemById(itemId);

        // 조회된 상품이 없는 경우 에러 응답 반환
        if (item == null) {
            return ApiResponse.onFailure(
                ErrorCode.ITEM_NOT_FOUND,           // 상품을 찾을 수 없다는 에러 코드
                "해당 상품을 찾을 수 없습니다."        // 사용자에게 보여줄 에러 메시지
            );
        }

        // 성공 시 상품 데이터와 함께 성공 응답 반환
        return ApiResponse.onSuccess(
            SuccessCode.ITEM_GET_SUCCESS,           // 상품 조회 성공 코드
            item                                    // 조회된 상품 데이터를 응답에 포함
        );
    }

    //카테고리 별 상품 조회
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "카테고리 별 상품 조회", description = "카테고리에 등록된 상품들을 조회합니다.")
    public ApiResponse<?> getItemsByCategory(@PathVariable Long categoryId) {
        List<ItemResponse> items = itemService.getItemsByCategory(categoryId);

        if (items.isEmpty()) {
            return ApiResponse.onSuccess(SuccessCode.CATEGORY_ITEMS_EMPTY, items);
        }

        return ApiResponse.onSuccess(SuccessCode.CATEGORY_ITEMS_GET_SUCCESS, items);
    }

}

//개별 상품 조회 및 카테고리 기반 상품 리스트 조회 기능을 제공
//Controller → Service → DTO 흐름
//API 일관성을 위해 ApiResponse와 Swagger @Operation을 적용