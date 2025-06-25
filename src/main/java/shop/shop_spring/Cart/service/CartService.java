package shop.shop_spring.Cart.service;

import shop.shop_spring.Cart.domain.Cart;
import shop.shop_spring.Cart.dto.CartDto;
import shop.shop_spring.Cart.dto.CartItemDto;
import shop.shop_spring.Member.domain.Member;
import shop.shop_spring.Order.domain.Order;

public interface CartService {
    CartDto getCartForMember(Long memberId);

    void addItemToCart(Long memberId, Long productId, int quantity);

    void updateItemQuantity(Long memberId, Long cartItemId, int newQuantity);

    void removeItemFromCart(Long memberId, Long cartItemId);

    boolean clearCart(Long memberId);

    Cart getCartEntityWithItemsAndProducts(Long memberId);

}
