package shop.shop_spring.Cart.service;

import shop.shop_spring.Cart.dto.CartDto;
import shop.shop_spring.Cart.dto.CartItemDto;
import shop.shop_spring.Member.domain.Member;
import shop.shop_spring.Order.domain.Order;

public interface CartService {
    CartDto getCartForMember(Long memberId);

    void addItemToCart(Long memberId, Long productId, int quantity);

    void updateItemQuantity(Long memberId, Long productId, int newQuantity);

    void removeItemFromCart(Long memberId, Long cartItemId);

    void clearCart(Long memberId);

    // 사용자 장바구니 결제 후 주문 생성
    //Order checkoutCart()

}
