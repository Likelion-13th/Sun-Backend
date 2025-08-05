package likelion13th.SunShop.service;

import jakarta.transaction.Transactional;
import likelion13th.SunShop.DTO.response.ItemResponse;
import likelion13th.SunShop.domain.Category;
import likelion13th.SunShop.domain.Item;
import likelion13th.SunShop.global.api.ErrorCode;
import likelion13th.SunShop.global.exception.GeneralException;
import likelion13th.SunShop.repository.CategoryRepository;
import likelion13th.SunShop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    //개별 상품 조회
    @Transactional
    public ItemResponse getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        return item != null ? ItemResponse.from(item) : null;
    }

    //카테고리 별 상품 조회
    @Transactional
    public List<ItemResponse> getItemsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));

        List<Item> items = itemRepository.findByCategories(category);

        return items.stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

}

//상품 개별 조회 및 카테고리별 상품 리스트 조회 로직을 담당하는 서비스
//GeneralException와 ItemResponse로 예외 처리와 응답포맷 변환을 일관되게 처리