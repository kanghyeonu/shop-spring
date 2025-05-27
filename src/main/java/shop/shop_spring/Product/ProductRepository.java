package shop.shop_spring.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shop_spring.Product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
