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

import java.nio.charset.StandardCharsets;
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

        private Member createTestMember() {
        Member member = new Member();
        member.setId(1L);
        member.setUsername("test@example.com");
        member.setPassword("test");
        member.setName("홍길동");
        member.setAddress("테스트시");
        member.setAddressDetail("테스트동");
        member.setBirthDate(LocalDate.of(1998, 1, 1));
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
        when(memberRepository.save(any(Member.class))).thenReturn(member);

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


    @Test
    void 회원_정보_모든_정보_수정(){
        // given
        Long existingId = 1L;
        String existingUsername = "test@example.com";
        String newRawPassword = "newPassword";
        String encodedNewRawPassword = "encodedPassword";
        LocalDate newBirthDate = LocalDate.of(1999,1, 1);
        String newAddress = "newAddress";
        String newAddressDetail = "newAddressDetail";
        String newNickname = "newNickname";

        Member existingMember = createTestMember();
        existingMember.setId(existingId);
        existingMember.setUsername(existingUsername);
        existingMember.setPassword("oldPassword");

        Member updatedMember = createTestMember();
        updatedMember.setId(existingId);
        updatedMember.setUsername(existingUsername);
        updatedMember.setPassword(newRawPassword);
        updatedMember.setBirthDate(newBirthDate);
        updatedMember.setAddress(newAddress);
        updatedMember.setAddressDetail(newAddressDetail);
        updatedMember.setNickname(newNickname);

        when(memberRepository.findByUsername(existingUsername)).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.encode(newRawPassword)).thenReturn(encodedNewRawPassword);
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        ArgumentCaptor<Member> argumentCaptor = ArgumentCaptor.forClass(Member.class);

        // when
        memberService.updateMember(updatedMember);

        // then
        verify(memberRepository, times(1)).findByUsername(existingUsername);
        verify(passwordEncoder, times(1)).encode(newRawPassword);
        verify(memberRepository, times(1)).save(argumentCaptor.capture());

        Member capturedMember = argumentCaptor.getValue();
        assertNotNull(capturedMember, "save()에 전달된 인자가 null임");
        assertEquals(encodedNewRawPassword, capturedMember.getPassword(), "기존 회원의 비밀번호가 업데이트 되지 않음");
        assertEquals(newBirthDate, capturedMember.getBirthDate(), "기존 회원의 생일이 업데이트 되지 않음");
        assertEquals(newAddress, capturedMember.getAddress(), "기존 회원의 주소가 업데이트 되지 않음");
        assertEquals(newAddressDetail, capturedMember.getAddressDetail(), "기존 회원의 상세 주소가 업데이트 되지 않음");
        assertEquals(newNickname, capturedMember.getNickname(), "기존 회원의 상세 주소가 업데이트 되지 않음");

        assertEquals(existingMember.getId(), capturedMember.getId());
        assertEquals(existingMember.getUsername(), capturedMember.getUsername());
    }

    @Test
    void 회원_정보_일부_정보_수정(){
        // given
        Long existingId = 1L;
        String existingUsername = "test@example.com";
        String newAddress = "newAddress";
        String newAddressDetail = "newAddressDetail";

        Member existingMember = createTestMember();
        existingMember.setId(existingId);
        existingMember.setUsername(existingUsername);

        Member updatedMember = createTestMember();
        updatedMember.setPassword(null);
        updatedMember.setBirthDate(null);
        updatedMember.setAddress(newAddress);
        updatedMember.setAddressDetail(newAddressDetail);
        updatedMember.setNickname(null);

        when(memberRepository.findByUsername(existingUsername)).thenReturn(Optional.of(existingMember));

        ArgumentCaptor<Member> argumentCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        // when
        memberService.updateMember(updatedMember);

        // then
        verify(memberRepository, times(1)).findByUsername(existingUsername);
        verify(memberRepository, times(1)).save(argumentCaptor.capture());
        verify(passwordEncoder, never()).encode(anyString());

        Member capturedMember = argumentCaptor.getValue();
        assertNotNull(capturedMember);
        // 바뀐거 체크
        assertEquals(newAddress, capturedMember.getAddress(), "기존 회원의 주소가 업데이트되지 않음");
        assertEquals(newAddressDetail, capturedMember.getAddressDetail(), "기존 회원의 주소가 업데이트 되지않음");
        // 바뀌지 앟은거 체크
        assertEquals("test", capturedMember.getPassword(), "비밀번호가 업데이트됨");
        assertEquals("테스트", capturedMember.getNickname(), "닉네임이 업데이트됨");
        assertEquals(LocalDate.of(1998, 1, 1), capturedMember.getBirthDate(), "생일이 업데이트됨");

        assertEquals(existingMember.getId(), capturedMember.getId());
        assertEquals(existingMember.getUsername(), capturedMember.getUsername());
    }

    @Test
    void 회원_정보_수정_없음(){
        // given
        String existingUsername = "test@example.com";

        Member existingMember = createTestMember();
        existingMember.setUsername(existingUsername);

        Member updatedMember = createTestMember();
        updatedMember.setUsername(existingUsername);
        updatedMember.setName(null);
        updatedMember.setNickname(null);
        updatedMember.setAddressDetail(null);
        updatedMember.setAddress(null);
        updatedMember.setPassword(null);

        when(memberRepository.findByUsername(existingUsername)).thenReturn(Optional.of(existingMember));

        ArgumentCaptor<Member> argumentCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        // when
        memberService.updateMember(updatedMember);

        // then
        verify(memberRepository, times(1)).findByUsername(existingUsername);
        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, times(1)).save(argumentCaptor.capture());

        Member capturedMember = argumentCaptor.getValue();
        Member temp = createTestMember();
        temp.setUsername(existingUsername);
        assertNotNull(capturedMember);
        assertEquals(temp.getPassword(), capturedMember.getPassword(), "기존 회원 비밀버호가 업데이트됨");
        assertEquals(temp.getAddress(), capturedMember.getAddress(), "기존 회원 주소가 업데이트됨");
        assertEquals(temp.getAddressDetail(), capturedMember.getAddressDetail(), "기존 회원 상세주소가 업데이트됨");
        assertEquals(temp.getBirthDate(), capturedMember.getBirthDate(), "기존 회원 생일이 업데이트됨");
        assertEquals(temp.getNickname(), capturedMember.getNickname(), "기존 회원의 닉네임이 업데이트됨");

        assertEquals(existingMember.getId(), capturedMember.getId());
        assertEquals(existingMember.getUsername(), capturedMember.getUsername());

    }
}