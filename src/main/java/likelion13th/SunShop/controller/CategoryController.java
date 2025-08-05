package likelion13th.SunShop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion13th.SunShop.DTO.response.ItemResponse;
import likelion13th.SunShop.global.api.ApiResponse;
import likelion13th.SunShop.global.api.SuccessCode;
import likelion13th.SunShop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카테고리", description = "카테고리 관련 API 입니다.")
@Slf4j
@RestController
@RequestMapping("/categorys")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    //특정 카테고리 상품 목록 조회
    @GetMapping("/{categoryId}/items")
    @Operation(summary = "카테고리 별 상품 조회", description = "카테고리에 등록된 상품들을 조회합니다.")
    public ApiResponse<?> getItemsByCategory(@PathVariable Long categoryId) {
        List<ItemResponse> items = categoryService.getItemsByCategory(categoryId);

        if (items.isEmpty()) {
            return ApiResponse.onSuccess(SuccessCode.CATEGORY_ITEMS_EMPTY, items);
        }
        return ApiResponse.onSuccess(SuccessCode.CATEGORY_ITEMS_GET_SUCCESS, items);
    }

    //카테고리 이름으로 조회
    @GetMapping("/name/{categoryName}/items")
    @Operation(summary = "카테고리 이름으로 상품 조회", description = "카테고리 이름으로 등록된 상품들을 조회합니다.")
    public ApiResponse<?> getItemsByCategoryName(@PathVariable String categoryName) {
        List<ItemResponse> items = categoryService.getItemsByCategoryName(categoryName);
        return ApiResponse.onSuccess(SuccessCode.CATEGORY_ITEMS_GET_SUCCESS, items);
    }

}

// Order API 패턴을 참고하여 카테고리 상품 조회 API 구현
//API 일관성을 위해 ApiResponse와 Swagger @Operation을 적용
//categorys/{id}/items, /categorys/name/{name}/items으로 상품 조회 가능
