package shop.shop_spring.Product;

import shop.shop_spring.Product.Dto.ProductCreationRequest;
import shop.shop_spring.Product.domain.Product;

public interface ProductService {
    Long createProduct(ProductCreationRequest product);

}
