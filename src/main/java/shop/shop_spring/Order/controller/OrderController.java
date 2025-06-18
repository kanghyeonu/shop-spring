package shop.shop_spring.Order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Dto.ApiResponse;
import shop.shop_spring.Order.Dto.SingleOrderItemRequest;
import shop.shop_spring.Order.sevice.OrderServiceImpl;
import shop.shop_spring.Security.MyUser;

import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;

    @PostMapping("/single-item/{productId}")
    public ResponseEntity placeSingleItemOrder(
            @PathVariable Long productId,
            @RequestBody SingleOrderItemRequest request,
            Authentication auth
            ){
        MyUser member = (MyUser) auth.getPrincipal();
        Long orderId = orderService.placeOrder(
                member.getId(),
                productId,
                request.getQuantity(),
                request.getDeliveryInfo());

        Map<String, String> responseData = ApiResponse.createResponseData("상품 아이디", orderId.toString());
        ApiResponse<Map<String, String>> successResponse = ApiResponse.success("상품 구매 완료", responseData);

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }
}
