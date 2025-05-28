package shop.shop_spring.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import shop.shop_spring.Category.CategoryRepository;
import shop.shop_spring.Category.CategoryService;
import shop.shop_spring.Category.domain.Category;
import shop.shop_spring.Product.Dto.ProductCreationRequest;
import shop.shop_spring.Product.domain.Product;
import shop.shop_spring.Product.domain.ProductDescription;
import shop.shop_spring.Product.enums.Status;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    @Override
    public Long createProduct(ProductCreationRequest request) {
        
        Product newProduct = productCreationRequestToProduct(request);
        
        if (request.getCategoryId() == null){
            throw new IllegalArgumentException("상품의 카테고리의 입력값 미선택");
        }
        Category category = categoryService.findById(request.getCategoryId());
        newProduct.setCategory(category);

        if (request.getDescription() != null){
            ProductDescription productDescription  = new ProductDescription();
            productDescription.setDescription(request.getDescription());
            newProduct.setDescription(productDescription);
        }

        validateProduct(newProduct);
        productRepository.save(newProduct);

        return newProduct.getId();
    }

    private void validateProduct(Product product) {
        /**
         * 어쩌구 저쩌구 검증 내용
         */
    }

    private Product productCreationRequestToProduct(ProductCreationRequest request){
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setUsername(request.getUsername());
        product.setStockQuantity(request.getStockQuantity());
        product.setThumbnailUrl(request.getThumbnailUrl());
        product.setStatus(Status.INACTIVE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return product;
    }


}
