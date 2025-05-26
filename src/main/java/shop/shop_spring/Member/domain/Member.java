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
    @Column(nullable = false) // NOT NULL 제약 조건을 설정합니다.
    @Enumerated(EnumType.STRING) // Enum 타입을 사용할 경우, 문자열 형태로 DB에 저장되도록 설정합니다.
    private Role role; // 사용자의 권한을 나타내는 칼럼입니다.

}
