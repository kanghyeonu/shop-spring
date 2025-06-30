package shop.shop_spring.Order.repository;


import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.shop_spring.Order.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 주문 Id로 주문 조회
     * 주문 관련 상품과 상품에 대한 상품 엔티티까지 Fetch Join하여 한 번에 로딩
     * @param orderId
     * @return
     */
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.product p " +
            "WHERE o.id = :orderId")
    Optional<Order> findByIdWithOrderItemsAndProduct(@Param("orderId") Long orderId);

    List<Order> findByOrdererId(Long memberId);
}
