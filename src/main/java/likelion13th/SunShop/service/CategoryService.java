package likelion13th.SunShop.service;

import jakarta.transaction.Transactional;
import likelion13th.SunShop.DTO.response.ItemResponse;
import likelion13th.SunShop.domain.Category;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    //특정 카테고리 상품 목록 조회
    @Transactional
    public List<ItemResponse> getItemsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));

        return category.getItems().stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    //카테고리 이름으로 조회
    @Transactional
    public List<ItemResponse> getItemsByCategoryName(String categoryName) {
        Category category = categoryRepository.findByCategory_name(categoryName)
                .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));

        return category.getItems().stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

}
