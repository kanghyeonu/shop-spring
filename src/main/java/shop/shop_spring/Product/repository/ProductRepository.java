package shop.shop_spring.Product.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import shop.shop_spring.Product.domain.Product;
import shop.shop_spring.Product.enums.Status;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor {
    List<Product> findAllByStatusNot(Status status, Sort sort);
}
