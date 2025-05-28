package shop.shop_spring.products;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shop_spring.Category.CategoryService;
import shop.shop_spring.Product.ProductRepository;
import shop.shop_spring.Product.ProductService;

@SpringBootTest
public class ProductServiceTest {

    @Mock
    private CategoryService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

}
