package shop.shop_spring.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shop_spring.Category.domain.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
}
