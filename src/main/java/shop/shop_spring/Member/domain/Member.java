package shop.shop_spring.Member.domain;

import jakarta.persistence.*;
import lombok.*;
import shop.shop_spring.Member.domain.enums.Role;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String    username;
    @Column(nullable = false)   private String                 password;
    @Column(nullable = false)   private String                 name;
    @Column(nullable = false)   private LocalDate              birthDate;
    @Column(nullable = false)   private String                 address;
    @Column(nullable = false)   private String                 addressDetail;
    @Column(nullable = true, unique = true) private String     nickname;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

}
