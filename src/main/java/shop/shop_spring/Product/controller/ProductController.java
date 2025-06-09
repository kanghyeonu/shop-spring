package shop.shop_spring.Product.controller;

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
import shop.shop_spring.Product.Dto.ProductSearchCondition;
import shop.shop_spring.Product.ProductForm;
import shop.shop_spring.Product.domain.Product;
import shop.shop_spring.Product.enums.Status;
import shop.shop_spring.Product.service.ProductServiceImpl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServiceImpl productService;

    @GetMapping("/{id}")
    String showDetail(@PathVariable Long id, Model model){
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "products/detail";
    }


    @GetMapping
    public String listProducts(
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "category", required = false) Long categoryId,
        Model model
    ){
        ProductSearchCondition searchCondition = new ProductSearchCondition();
        searchCondition.setProductTitle(title);
        searchCondition.setCategoryId(categoryId);

        List<Status> statuses = new ArrayList<>();
        statuses.add(Status.ACTIVE);
        statuses.add(Status.SOLD_OUT);
        searchCondition.setStatuses(statuses);

        List<Product> products = productService.searchProducts(searchCondition);

        model.addAttribute("products", products);
        return "products/list";
    }

    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String createForm(Model model, Authentication auth){
        model.addAttribute("username", auth.getName());
        return "products/productForm";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<Map<String, String>>> createProduct(@RequestBody ProductCreationRequest request){
        Long Id = productService.createProduct(request);

        Map<String, String> responseData = ApiResponse.createResponseData("상품 아이디", Id.toString());
        ApiResponse<Map<String, String> > successResponse = ApiResponse.success("상품 등록 완료", responseData);

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
