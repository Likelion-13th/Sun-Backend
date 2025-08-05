package likelion13th.SunShop.repository;

import likelion13th.SunShop.domain.Category;
import likelion13th.SunShop.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    //save, findById, findAll 같은 기본 기능 자동으로 제공됨
    //카테고리별 아이템 조회
    List<Item> findByCategories(Category category);
}