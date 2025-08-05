package likelion13th.SunShop.repository;

import likelion13th.SunShop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 카테고리 이름으로 조회 (중복 방지)
    Optional<Category> findByCategory_name(String categoryName);
    Optional<Category> findById(Long categoryid);

}

//카테고리 정보를 DB에서 조회·저장하는 JPA 레포지토리 인터페이스로 이름과 id를 통해 조회한다.
