package shop.shop_spring.Cart.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.shop_spring.Cart.domain.Cart;
import shop.shop_spring.Cart.domain.CartItem;
import shop.shop_spring.Cart.dto.CartDto;
import shop.shop_spring.Cart.repository.CartItemRepository;
import shop.shop_spring.Cart.repository.CartRepository;
import shop.shop_spring.Member.domain.Member;
import shop.shop_spring.Member.service.MemberService;
import shop.shop_spring.Product.domain.Product;
import shop.shop_spring.Product.service.ProductService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    private final MemberService memberService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    @Override
    public CartDto getCartForMember(Long memberId) {
        Optional<Cart> cart = cartRepository.findByMemberIdWithItemsAndProducts(memberId);
        if (cart.isEmpty() || cart.get().getCartItems().isEmpty()){
            return null;
        }
        return CartDto.fromEntity(cart.get());
    }

    @Transactional
    @Override
    public void addItemToCart(Long memberId, Long productId, int quantity) {
        Member member = memberService.findById(memberId);
        Product product = productService.findById(productId);
        Cart memberCart = cartRepository.findByMemberIdWithItemsAndProducts(memberId)
                .orElseGet(() -> {
                   Cart newCart = Cart.builder()
                           .member(member)
                           .build();
                   return cartRepository.save(newCart);
                });


        CartItem cartItem = null;
        Optional<CartItem> existingCartItemOptional = cartItemRepository.
                findByCartIdAndProductId(memberCart.getId(), productId);

        if (existingCartItemOptional.isPresent()){
            cartItem = existingCartItemOptional.get();
            cartItem.setQuantity(quantity);
        } else {
            cartItem = CartItem.builder()
                    .cart(memberCart)
                    .product(product)
                    .quantity(quantity)
                    .build();
        }

        memberCart.addCartItem(cartItem);

        cartItemRepository.save(cartItem);

    }

    @Override
    public void updateItemQuantity(Long memberId, Long productId, int newQuantity) {

    }

    @Override
    public void removeItemFromCart(Long memberId, Long productId) {

    }

    @Transactional
    @Override
    public void clearCart(Long memberId) {
        Optional<Cart> cart = cartRepository.findByMemberIdWithItemsAndProducts(memberId);
        if (cart.isEmpty() || cart.get().getCartItems().isEmpty()){
            return;
        }
        cart.get().getCartItems().clear();
    }

}
