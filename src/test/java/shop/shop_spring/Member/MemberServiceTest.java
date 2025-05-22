package shop.shop_spring.Member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static  org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;


@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired private MemberService memberService;
    //@Autowired private MemberRepository memberRepository;

    private Member createTestMember() {
        Member member = new Member();
        member.setUsername("test@example.com");
        member.setPassword("test");
        member.setName("홍길동");
        member.setAddress("테스트시");
        member.setAddressDetail("테스트동");
        member.setBirthDate(LocalDate.now());
        member.setNickname("테스트");

        return member;
    }
    @Test
    void 회원가입_성공(){
        // given
        Member member = createTestMember();

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findById(saveId);
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    void 중복_회원가입_실패(){
        // given
        Member member = createTestMember();
        Member member2 = createTestMember();

        // when
        memberService.join(member);
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, ()-> memberService.join(member2));

        // then
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원");
    }

    // 멤버 정보 변경
}
