package likelion13th.SunShop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private String category_name;

    @ManyToMany
    @JoinTable(
            name = "item_category", // 생성될 중간 테이블 이름
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    public Category(String category_name) {
        this.category_name = category_name;
    }
}

// Order.java 패턴을 참고하여 Category 도메인을 구현
// 중간 엔티티 없이 @ManyToMany로 Item과 직접 연결
