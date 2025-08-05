package likelion13th.SunShop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import likelion13th.SunShop.domain.entity.BaseEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private String item_name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String itemLeft;

    @Column(nullable = false)
    @Setter
    private boolean isNew= false;

    //Category와 다대다 연관관계 설정
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //Order과 일대다 연관관계 설정
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    public Item(String item_name, int price, String imagePath, String brand, boolean isNew) {
        this.item_name = item_name;
        this.price = price;
        this.imagePath = imagePath;
        this.brand = brand;
        this.isNew= isNew;
    }
}

//판매 상품 정보를 담고있으며, Order, Category와 연결됨.
//ERD에 있던 itemcategory는 중간 엔티티 없이 단순한 @ManyToMany 매핑으로 처리