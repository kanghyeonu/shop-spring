package shop.shop_spring.Member;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class MemberForm {
    private Long id;
    private String Email;
    private String password;
    private String name;
    private String birthDate;
    private String address;
    private String addressDetail;
    private String nickname;
}
