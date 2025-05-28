package shop.shop_spring.Member.Dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {
    private String username;
    private String password;
    private String birthDate;
    private String address;
    private String addressDetail;
    private String nickName;
}
