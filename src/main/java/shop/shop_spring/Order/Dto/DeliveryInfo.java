package shop.shop_spring.Order.Dto;

import lombok.*;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfo {
    private String receiverName;
    private String address;
    private String deliveryMessage;
}
