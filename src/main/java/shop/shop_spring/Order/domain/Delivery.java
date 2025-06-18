package shop.shop_spring.Order.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "delivery")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "order")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_name")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order; // 배송하는 주문

    @Column(name = "receiver_name", nullable = false)
    private String receiverName; // 받는 사람

    @Column(name = "address", nullable = false)
    private String address; // 주소

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DeliveryStatus status; // 배송 상태

    @Column(name = "delivery_message", nullable = true)
    private String deliveryMessage; // 배송 메세지


    public enum DeliveryStatus{
        READY,
        IN_TRANSIT,
        DELIVERED
    }
}
