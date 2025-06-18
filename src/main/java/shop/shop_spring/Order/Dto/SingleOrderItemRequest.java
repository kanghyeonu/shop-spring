package shop.shop_spring.Order.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleOrderItemRequest {
    private int quantity;
    private DeliveryInfo deliveryInfo;
}
