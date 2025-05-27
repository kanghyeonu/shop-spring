package shop.shop_spring.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.shop_spring.Category.domain.Category;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductForm {
    String title;
    BigDecimal price;
    String username;
    Integer stockQuantity;
    String description;
    Long categoryId;
    String thumbnailUrl;
}
