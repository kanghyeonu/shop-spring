package shop.shop_spring.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String    Email;
    @Column(nullable = false)   private String                 password;
    @Column(nullable = false)   private String                 name;
    @Column(nullable = false)   private LocalDate              birthDate;
    @Column(nullable = false)   private String                 address;
    @Column(nullable = false)   private String                 addressDetail;
    @Column(nullable = true, unique = true) private String     nickname;
}
