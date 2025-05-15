package shop.shop_spring.Security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import shop.shop_spring.Member.enums.Role;

import java.util.Collection;

@Getter
@Setter
public class MyUser extends User {
    // 필요 정보 추가
    private Long id;
    private String name;
    private String nickname;

    public MyUser(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);
    }
}
