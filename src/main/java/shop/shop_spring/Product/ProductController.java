package shop.shop_spring.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Dto.ApiResponse;
import shop.shop_spring.Product.Dto.ProductCreationRequest;


import java.time.LocalDateTime;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<Map<String, String>>> createProduct(@RequestBody ProductCreationRequest request){
        productService.createProduct(request);

        List<String> productInfoKeys = new ArrayList<>();
        List<String> productInfoValues = new ArrayList<>();
        Map<String, String> responseData = ApiResponse.createResponseData(productInfoKeys, productInfoValues);
        ApiResponse<Map<String, String>> successResponse = ApiResponse.success("상품 등록 완료", responseData);

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    private ProductCreationRequest formToProductCreationRequest(ProductForm form){
        ProductCreationRequest request = new ProductCreationRequest();

        request.setTitle(form.getTitle());
        request.setPrice(form.getPrice());
        request.setUsername(form.getUsername());
        request.setStockQuantity(form.getStockQuantity());
        request.setThumbnailUrl(form.getThumbnailUrl());
        request.setDescription(form.getDescription());
        request.setCategoryId(form.getCategoryId());

        return request;
    }

}
