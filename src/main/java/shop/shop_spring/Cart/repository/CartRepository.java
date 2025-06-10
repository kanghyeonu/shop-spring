package shop.shop_spring.Cart.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shop_spring.Cart.domain.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product WHERE c.member.id = :memberId")
    Optional<Cart> findByMemberIdWithItemsAndProducts(@Param("memberId") Long memberId);
}
