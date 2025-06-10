package shop.shop_spring.Cart.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.shop_spring.Cart.domain.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
