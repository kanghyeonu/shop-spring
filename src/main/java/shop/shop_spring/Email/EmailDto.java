package shop.shop_spring.Email;

import jakarta.websocket.server.ServerEndpoint;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {
    private String email;
    private String title;
    private String text;
}
