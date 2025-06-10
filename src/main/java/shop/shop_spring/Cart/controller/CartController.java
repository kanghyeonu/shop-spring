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

        model.addAttribute("cart", cartDto);

        return "/members/my-page/cartItems";
    }

    @PostMapping("/items")
    public ResponseEntity addProductToCart(@RequestBody CartAddRequest addRequest, Authentication auth){
        MyUser member = (MyUser) auth.getPrincipal();

        cartService.addItemToCart(member.getId(), addRequest.getProductId(),addRequest.getQuantity());

        ApiResponse<Void> response = ApiResponse.successNoData("추가 성공");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
