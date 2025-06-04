package shop.shop_spring.Product;

import shop.shop_spring.Product.Dto.ProductCreationRequest;
import shop.shop_spring.Product.Dto.ProductSearchCondition;
import shop.shop_spring.Product.Dto.ProductUpdateRequest;
import shop.shop_spring.Product.domain.Product;

import java.util.List;

public interface ProductService {

    Long createProduct(ProductCreationRequest product);

    List<Product> searchProducts(ProductSearchCondition productSearchCondition);

    Product findById(Long id);

    void updateProduct(String username, Long productId, ProductUpdateRequest updateRequest);

    void deleteProduct(String username, Long pd);
}
