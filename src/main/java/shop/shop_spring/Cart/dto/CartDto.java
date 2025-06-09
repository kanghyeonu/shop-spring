package shop.shop_spring.Cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.jndi.JndiLocatorDelegate;
import shop.shop_spring.Cart.domain.Cart;
import shop.shop_spring.Cart.domain.CartItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long id;
    private List<CartItemDto> items;
    private int totalItemCount;
    private int totalProductsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartDto fromEntity(Cart cart){
        List<CartItemDto> cartItemDtos = cart.getCartItems().stream()
                .map(CartItemDto::fromEntity)
                .collect(Collectors.toList());

        int totalItems = cartItemDtos.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();
        int totalProducts = cartItemDtos.size();

        return new CartDto(
                cart.getId(),
                cartItemDtos,
                totalItems,
                totalProducts,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }

}
