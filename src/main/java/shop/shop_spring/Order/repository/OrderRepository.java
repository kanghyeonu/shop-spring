package shop.shop_spring.Order.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import shop.shop_spring.Order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
