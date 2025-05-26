package shop.shop_spring.Product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shop.shop_spring.Category.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, precision = 10)
    private BigDecimal price;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(nullable = false)
    private Integer stockQuantity;

    @OneToOne(mappedBy = "product",
                cascade = CascadeType.ALL, // 상품 삭제 시 상세 설명eh 삭제
                orphanRemoval = true, // 고아 객체도 제거
                fetch = FetchType.LAZY)
    private ProductDetail productDetail;

    public void setProductDetail(ProductDetail productDetail){
        if (this.productDetail != null) {
            this.productDetail.setProduct(null);
        }
        this.productDetail = productDetail;
        if (productDetail != null){
            productDetail.setProduct(this);
        }
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
