package shop.shop_spring.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shop_spring.Member.domain.Member;

import java.util.Optional;

public interface MemberRepository  extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
}
