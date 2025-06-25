package shop.shop_spring.Order.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemOrderRequest {
    private DeliveryInfo deliveryInfo;
    private String paymentMethod;
}
