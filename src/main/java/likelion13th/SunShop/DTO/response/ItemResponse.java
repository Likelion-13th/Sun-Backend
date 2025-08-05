package likelion13th.SunShop.DTO.response;

import likelion13th.SunShop.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemResponse {
    private Long itemId;
    private String itemName;
    private int price;
    private String imagePath;
    private String brand;
    private boolean isNew;
    private String itemLeft;

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getItem_name(),
                item.getPrice(),
                item.getImagePath(),
                item.getBrand(),
                item.isNew(),
                item.getItemLeft()
        );
    }
}
