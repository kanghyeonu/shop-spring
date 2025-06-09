package shop.shop_spring.Category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.shop_spring.Category.service.CategoryService;
import shop.shop_spring.Category.domain.Category;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/parents")
    @ResponseBody
    public List<Category> getParentCategories() {
        return categoryService.findByParentIsNull();
    }

    @GetMapping("/children")
    @ResponseBody
    public List<Category> getChildCategories(@RequestParam Long parentId){
        return categoryService.findByParentId(parentId);
    }
}
