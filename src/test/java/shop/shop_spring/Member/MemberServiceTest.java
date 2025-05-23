package shop.shop_spring.Member;

import org.assertj.core.api.MapAssert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import shop.shop_spring.Exception.DataNotFoundException;

import static  org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;


@SpringBootTest
@Transactional
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    //@Autowired private MemberRepository memberRepository;

    private Member createTestMember() {
        Member member = new Member();
        member.setId(1L);
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
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";

        Long expectedMemberId = 1L;

        Member inputMember = createTestMember();
        inputMember.setPassword(rawPassword);
        Member savedMember = createTestMember();
        savedMember.setId(expectedMemberId);
        savedMember.setPassword(encodedPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepository.save(memberArgumentCaptor.capture())).thenReturn(savedMember);

        // when
        Long savedMemberId = memberService.join(inputMember);

        // then
        assertNotNull(savedMemberId);
        assertEquals(expectedMemberId, savedMemberId);

        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(memberRepository, times(1)).save(any(Member.class));

        Member capturedMember = memberArgumentCaptor.getValue();
        assertNotNull(capturedMember);
        assertEquals(encodedPassword, capturedMember.getPassword(), "save 메서드에 전달된 객체의 비밀번호는 암호화 되야함");
        assertEquals(encodedPassword, inputMember.getPassword(), "서비스 메서드 실행 후 입력 member 비밀번호는 암호화되야함");
        assertEquals(inputMember.getUsername(), capturedMember.getUsername(), "저장 후의 member username이 다름");

    }

    @Test
    void 회원가입_실패_중복() {
        String duplicatedUsername = "dupUsername";
        // given
        Member inputMember = createTestMember();
        inputMember.setUsername(duplicatedUsername);
        Member exsitingMember = createTestMember();
        exsitingMember.setUsername(duplicatedUsername);

        when(memberRepository.findByUsername(duplicatedUsername)).thenReturn(Optional.of(exsitingMember));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> memberService.join(inputMember));

        assertEquals("이미 존재하는 회원", exception.getMessage());

        verify(memberRepository, times(1)).findByUsername(duplicatedUsername);

        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void 사용자ID기반_존재_회원_조회_성공(){
        // given
        Long existingMemberId = 1L;
        Member member = createTestMember();
        member.setId(existingMemberId);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        Member foundMember = memberService.findById(existingMemberId);

        //then
        assertNotNull(foundMember);
        assertEquals(existingMemberId, foundMember.getId());

        verify(memberRepository, times(1)).findById(existingMemberId);

    }

    @Test
    void 사용자ID기반_존재하지_않는_회원_조회_실패(){
        // given
        Long nonExistingMemberId = 1234L;

        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
        //when & then
        assertThrows(DataNotFoundException.class, ()->
            memberService.findById(nonExistingMemberId)
        );

        verify(memberRepository, times(1)).findById(nonExistingMemberId);
    }

    @Test
    void 사용자이메일기반_존재_회원_조회_성공(){
        // given
        String existingUsername = "test@example.com";
        Member member = createTestMember();
        member.setUsername(existingUsername);

        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));

        // when
        Member foundMember = memberService.findByUsername(existingUsername);

        // then
        assertNotNull(foundMember);
        assertEquals(existingUsername, foundMember.getUsername());

        verify(memberRepository, times(1)).findByUsername(existingUsername);
    }

    @Test
    void 사용자이메일기반_존재하지_않는_회원_조회_실패(){
        // given
        String nonExistingUsername = "test@example.com";

        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThrows(DataNotFoundException.class, ()->
                memberService.findByUsername(nonExistingUsername)
        );

        verify(memberRepository, times(1)).findByUsername(nonExistingUsername);

    }

    @Test
    void 비밀번호_변경_성공(){
        //given
        String username = "test@example.com";
        String newRawPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";

        Member member = createTestMember();
        member.setId(1L);
        member.setUsername(username);
        member.setPassword("oldPassword");

        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(newRawPassword)).thenReturn(encodedNewPassword);

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        memberService.updatePassword(username, newRawPassword);

        // then
        verify(memberRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(newRawPassword);
        verify(memberRepository, times(1)).save(memberArgumentCaptor.capture());

        Member capturedMember = memberArgumentCaptor.getValue();

        assertNotNull(capturedMember);
        assertEquals(encodedNewPassword, capturedMember.getPassword(), "Member 객체의 비밀번호가 업데이트 되어야함");
        assertEquals(encodedNewPassword, member.getPassword(), "비밀번호 업데이트 후 기존 Member 객체도 업데이트 되어야함");
        assertEquals(member.getId(), capturedMember.getId());


    }
}
