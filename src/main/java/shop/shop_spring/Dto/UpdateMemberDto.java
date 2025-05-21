package shop.shop_spring.Dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberDto {
    private String username;
    private String password;
    private String birthDate;
    private String address;
    private String addressDetail;
    private String nickName;
}
