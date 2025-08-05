package likelion13th.SunShop.repository;

import likelion13th.SunShop.domain.Category;
import likelion13th.SunShop.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategories(Category category);
}

//상품 데이터를 DB에서 조회·저장하는 레포지토리
//카테고리 기반의 상품 목록 조회 할 수 있다.