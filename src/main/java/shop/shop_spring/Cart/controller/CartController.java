package shop.shop_spring.Cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Cart.dto.CartAddRequest;
import shop.shop_spring.Cart.dto.CartDto;
import shop.shop_spring.Cart.dto.CartItemUpdateRequest;
import shop.shop_spring.Cart.service.CartService;
import shop.shop_spring.Cart.service.CartServiceImpl;
import shop.shop_spring.Dto.ApiResponse;
import shop.shop_spring.Security.MyUser;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartService;

    @GetMapping("/items")
    public String showCart(Authentication auth, Model model){
        MyUser member = (MyUser) auth.getPrincipal();

        CartDto cartDto = cartService.getCartForMember(member.getId());

        boolean isLoggedIn = auth != null && auth.isAuthenticated();
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("cart", cartDto);

        return "members/my-page/cartItems";
    }

    @PostMapping("/items")
    public ResponseEntity addProductToCart(@RequestBody CartAddRequest addRequest, Authentication auth){
        MyUser member = (MyUser) auth.getPrincipal();

        cartService.addItemToCart(member.getId(), addRequest.getProductId(),addRequest.getQuantity());

        ApiResponse<Void> response = ApiResponse.successNoData("추가 성공");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity removeProductFromCart(@PathVariable Long id, Authentication auth){
        MyUser member = (MyUser) auth.getPrincipal();
        cartService.removeItemFromCart(member.getId(), id);

        ApiResponse<Void> response = ApiResponse.successNoData("상품 삭제 성공");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    public ResponseEntity clearCart(Authentication auth){
        MyUser member = (MyUser) auth.getPrincipal();

        boolean cleared = cartService.clearCart(member.getId());

        String message = cleared ? "장바구니 비우기 성공" : "이미 비어있는 장바구니";

        ApiResponse<Void> response  = ApiResponse.successNoData(message);;

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity updateQuantity(@PathVariable Long id, @RequestBody CartItemUpdateRequest updateRequest, Authentication auth){
        MyUser member = (MyUser) auth.getPrincipal();
        cartService.updateItemQuantity(member.getId(), id ,updateRequest.getQuantity());

        ApiResponse<Void> response = ApiResponse.successNoData("상품 개수 업데이트");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
