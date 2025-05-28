package shop.shop_spring.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.shop_spring.Category.domain.Category;
import shop.shop_spring.Exception.DataNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findByParentIsNull() {
        var result = categoryRepository.findByParentIsNull();
        if (result.size() == 0){
            throw new DataNotFoundException("DB 오류");
        }
        return result;
    }

    public List<Category> findByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public Category findById(Long categoryId) {
        Optional<Category> result = categoryRepository.findById(categoryId);
        if (result.isEmpty()){
            throw new DataNotFoundException("잘못된 카테고리 아이디");
        }
        return result.get();
    }
}
