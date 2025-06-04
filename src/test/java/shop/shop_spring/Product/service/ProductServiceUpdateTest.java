package shop.shop_spring.Product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import shop.shop_spring.Category.CategoryService;
import shop.shop_spring.Category.domain.Category;
import shop.shop_spring.Exception.DataNotFoundException;
import shop.shop_spring.Product.Dto.ProductCreationRequest;
import shop.shop_spring.Product.Dto.ProductSearchCondition;
import shop.shop_spring.Product.Dto.ProductUpdateRequest;
import shop.shop_spring.Product.ProductRepository;
import shop.shop_spring.Product.ProductServiceImpl;
import shop.shop_spring.Product.domain.Product;
import shop.shop_spring.Product.domain.ProductDescription;
import shop.shop_spring.Product.specification.ProductSpecification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProductServiceUpdateTest {
    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product existingProduct;
    private ProductUpdateRequest updateRequest;


    @BeforeEach
    void setUp(){
        existingProduct = createTestProduct(1L);
    }

    private Product createTestProduct(Long id){
        Product product = new Product();
        product.setId(id);
        product.setTitle("테스트 상품");
        product.setPrice(BigDecimal.valueOf(10000));
        product.setUsername("테스트 판매자");
        product.setStockQuantity(50);
        ProductDescription productDescription = new ProductDescription();
        productDescription.setDescription("테스트 상품 상세 설명");
        product.setDescription(productDescription);
        product.setThumbnailUrl("http://example.com/thum.jpg");
        Category category = new Category();
        category.setId(1L);
        category.setName("테스트 카테고리");
        product.setCategory(category);

        return product;
    }

    @Test
    void 상품_수정_성공(){

    }
}
