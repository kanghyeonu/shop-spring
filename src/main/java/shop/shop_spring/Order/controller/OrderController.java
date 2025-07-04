package shop.shop_spring.Order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Dto.ApiResponse;
import shop.shop_spring.Order.Dto.CartItemOrderRequest;
import shop.shop_spring.Order.Dto.OrderDetailDto;
import shop.shop_spring.Order.Dto.OrderSummaryDto;
import shop.shop_spring.Order.Dto.SingleItemOrderRequest;
import shop.shop_spring.Order.sevice.OrderServiceImpl;
import shop.shop_spring.Payment.Dto.PaymentInitiationResponse;
import shop.shop_spring.Security.MyUser;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;

    @PostMapping("/single-item/{productId}")
    public ResponseEntity placeSingleItemOrder(
            @PathVariable Long productId,
            @RequestBody SingleItemOrderRequest request,
            Authentication auth){
        MyUser member = (MyUser) auth.getPrincipal();

        PaymentInitiationResponse initiationResponse = orderService.placeOrder(
                member.getId(),
                productId,
                request.getQuantity(),
                request.getDeliveryInfo(),
                request.getPaymentMethod());

        ApiResponse<PaymentInitiationResponse> response = ApiResponse.success(
                "주문 시작 및 결제 요청 정보 생성", initiationResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/cart-items")
    public ResponseEntity placeCartItemOrder(
            @RequestBody CartItemOrderRequest request,
            Authentication auth){

        MyUser member = (MyUser) auth.getPrincipal();

        PaymentInitiationResponse initiationResponse = orderService.placeCartOrder(
                member.getId(),
                request.getDeliveryInfo(),
                request.getPaymentMethod());

        ApiResponse<PaymentInitiationResponse> response = ApiResponse.success(
                "주문 시작 및 결제 요청 정보 생성", initiationResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/my-orders")
    public String getMyOrderPage(Authentication auth, Model model){
        MyUser member = (MyUser) auth.getPrincipal();
        List<OrderSummaryDto> orderSummaries = orderService.getOrdersByMember(member.getId());

        model.addAttribute("orderSummaries", orderSummaries);
        return "members/my-page/orders";
    }

    @GetMapping("/{orderId}")
    public String getOrderDetailsPage(@PathVariable("orderId") Long orderId, Authentication auth, Model model){
        MyUser member = (MyUser) auth.getPrincipal();

        OrderDetailDto orderDetailDto = orderService.getOrderDetails(member.getId(), orderId);

        model.addAttribute("orderDetails", orderDetailDto);
        return "orders/details";

    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Authentication auth){
        MyUser member = (MyUser) auth.getPrincipal();

        orderService.cancelOrder(member.getId(), orderId);

        ApiResponse<Void> response = ApiResponse.successNoData("취소됨");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
