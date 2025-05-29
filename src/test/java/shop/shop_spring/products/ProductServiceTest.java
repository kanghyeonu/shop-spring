package shop.shop_spring.products;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import shop.shop_spring.Category.CategoryService;
import shop.shop_spring.Category.domain.Category;
import shop.shop_spring.Product.Dto.ProductCreationRequest;
import shop.shop_spring.Product.ProductRepository;
import shop.shop_spring.Product.ProductService;
import shop.shop_spring.Product.ProductServiceImpl;
import shop.shop_spring.Product.domain.Product;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

    @Test
    void 상품_등록_성공(){
        ProductCreationRequest request = new ProductCreationRequest();
        request.setTitle("테스트 상품");
        request.setPrice(BigDecimal.valueOf(10000));
        request.setUsername("테스트 판매자");
        request.setStockQuantity(50);
        request.setDescription("테스트 상품 상세 설명");
        request.setThumbnailUrl("http://example.com/thum.jpg");
        request.setCategoryId(1L);

        Category mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("모킹 카테고리");

        when(categoryService.findById(anyLong())).thenReturn(mockCategory);

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productToSave = invocation.getArgument(0); // save 호출 시 넘겨받은 Product 객체
            // 실제 JPA save 동작을 모방하여 ID 설정 (DB에서 할당받은 것처럼 시뮬레이션)
            if (productToSave.getId() == null) { // ID가 아직 설정되지 않았다면 (새로운 객체인 경우)
                productToSave.setId(101L); // 테스트용 가짜 ID 설정
            }
            return productToSave; // ID가 설정된 (동일한 인스턴스) 객체 반환
        });

        // when
        productService.createProduct(request);

        // then
        verify(categoryService, times(1)).findById(1L);
        verify(productRepository, times(1)).save(productArgumentCaptor.capture());

        Product capturedProduct = productArgumentCaptor.getValue();

        assertNotNull(capturedProduct);
        assertEquals(request.getUsername(), capturedProduct.getUsername(), "판매자 이름이 다름");
        assertNotNull(capturedProduct.getCategory(), "카테고리가 있어야함");
        assertEquals(mockCategory.getId(), capturedProduct.getCategory().getId(), "카테고리가 같아야함");
        assertNotNull(capturedProduct.getDescription(), "상품 설명이 설정되지 않음");
        assertEquals(request.getDescription(), capturedProduct.getDescription().getDescription(), "상품 설명이 다름");
        assertEquals(101L, capturedProduct.getId(), "저장되야하는 상품 id와 값이 다름");
    }

}
