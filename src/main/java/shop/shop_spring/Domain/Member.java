package shop.shop_spring.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String    email;
    @Column(nullable = false)   private String                 password;
    @Column(nullable = false)   private String                 name;
    @Column(nullable = false)   private LocalDate              birthDate;
    @Column(nullable = false)   private String                 address;
    @Column(nullable = false)   private String                 addressDetail;
    @Column(nullable = true, unique = true) private String     nickname;
}
