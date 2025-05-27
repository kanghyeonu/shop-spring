package shop.shop_spring.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Category.domain.Category;
import shop.shop_spring.Dto.ApiResponse;
import shop.shop_spring.Product.domain.Product;
import shop.shop_spring.Product.domain.ProductDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String createForm(Model model, Authentication auth){
        model.addAttribute("username", auth.getName());
        return "products/productForm";
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> createProduct(@RequestBody ProductForm form){
//        Product product = formToProduct(form);

//        Product newProduct = productService.createProduct(product);

        System.out.println(form.getTitle());
        System.out.println(form.getUsername());
        System.out.println(form.getPrice());
        System.out.println(form.getCategoryId());

        List<String> productInfoKeys = new ArrayList<>();
        List<String> productInfoValues = new ArrayList<>();
        Map<String, String> responseData = ApiResponse.createResponseData(productInfoKeys, productInfoValues);
        ApiResponse<Map<String, String>> successResponse = ApiResponse.success("상품 등록 성공", responseData);

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    private Product formToProduct(ProductForm form){
        Product product = new Product();
        product.setTitle(form.getTitle());
        product.setPrice(form.getPrice());
        product.setUsername(form.getUsername());
        product.setStockQuantity(form.getStockQuantity());

        ProductDescription productDescription = new ProductDescription();
        productDescription.setDescription(form.getDescription());
        product.setDescription(productDescription);

//        Category category = new Category();
//        category.setName(form.getCategory());

        return product;

    }
}
