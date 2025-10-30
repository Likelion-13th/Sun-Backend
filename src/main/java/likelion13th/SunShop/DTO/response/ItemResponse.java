package likelion13th.SunShop.DTO.response;



import com.fasterxml.jackson.annotation.JsonProperty;
import likelion13th.SunShop.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private int price;
    private String brand;
    private String imagePath;
    private boolean isNew;

    @JsonProperty("isNew")
    public boolean getIsNew() {
        return isNew;
    }

    // Item → ItemResponseDto 변환
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getItemName(),
                item.getPrice(),
                item.getBrand(),
                item.getImagePath(),
                item.isNew()
        );
    }
}


// orderResponse 패턴과 동일하게 적용
// 클라이언트에 상품 정보를 응답할 때 사용
