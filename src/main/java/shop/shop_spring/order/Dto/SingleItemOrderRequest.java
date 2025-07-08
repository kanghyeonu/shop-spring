package shop.shop_spring.order.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleItemOrderRequest {
    private int quantity;
    private DeliveryInfo deliveryInfo;
    private String paymentMethod;
}
