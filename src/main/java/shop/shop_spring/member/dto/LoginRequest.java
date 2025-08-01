package shop.shop_spring.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {
    @Schema(description = "이메일 주소", example = "user@example.com", required = true)
    private String username;

    @Schema(description = "비밀번호", example = "yourPassword123!", required = true)
    private String password;
}
