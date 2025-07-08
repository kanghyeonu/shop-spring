package shop.shop_spring.order.Dto;

import lombok.*;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfo {
    private String receiverName;
    private String address;
    private String addressDetail;
    private String deliveryMessage;
}
