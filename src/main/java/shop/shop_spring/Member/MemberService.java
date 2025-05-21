package shop.shop_spring.Member;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.shop_spring.Dto.EmailDto;
import shop.shop_spring.Dto.UpdateMemberDto;
import shop.shop_spring.Email.EmailServiceImpl;
import shop.shop_spring.Exception.DataNotFoundException;
import shop.shop_spring.Redis.RedisEmailAuthentication;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final EmailServiceImpl emailService;
    private final RedisEmailAuthentication redisEmailAuthentication;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(Member member){
        validateMember(member);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
        return member.getId();
    }

    private void validateMember(Member member) {
        /**
         *  이메일: 고유
         *  비밀번호
         *  주소 & 상세 주소
         *  생일
         */
        validateDuplicateMember(member.getUsername());
    }

    public void validateDuplicateMember(String username) {
        memberRepository.findByUsername(username)
                .ifPresent(m -> {
                    throw new IllegalArgumentException("이미 존재하는 회원");
                });
    }


    public void sendAuthenticationCode(String email) throws MessagingException, UnsupportedEncodingException {
        String code = createRandomCode();
        redisEmailAuthentication.setEmailAuthenticationExpire(email, code, 6L);

        String text = "";
        text += "안녕하세요. myShop입니다.";
        text += "<br/><br/>";
        text += "인증코드 보내드립니다. 5분 내로 입력해주세요";
        text += "<br/><br/>";
        text += "인증코드 : <b>"+code+"</b>";

        EmailDto data = EmailDto.builder()
                .email(email)
                .title("이메일 인증코드 발송 메일입니다.")
                .text(text)
                .build();
        /* 입력한 이메일로 인증코드 발송 */
        emailService.sendMail(data);
    }

    private String createRandomCode(){
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        // 6자리 이하 정수에서 0 padding
        String sixDigitCode = String.format("%06d", randomNumber);

        return sixDigitCode;

    }

    public void validateAuthenticationCode(String email, String code) {
        String existCode = redisEmailAuthentication.getEmailAuthenticationCode(email);
        if (existCode == null){
            throw new DataNotFoundException("유효하지 않은 사용자");
        }

        if (!existCode.equals(code)){
            throw new DataNotFoundException("유효하지 않은 인증 번호");
        }
    }

    public Member findById(Long memberId) {
        Optional<Member> result = memberRepository.findById(memberId);
        if (result.isEmpty()){
            throw new DataNotFoundException("존재하지 않는 사용자 정보: ID");
        }
        return result.get();
    }
    public Member findByUsername(String username) {
        Optional<Member> result = memberRepository.findByUsername(username);
        if (result.isEmpty()){
            throw new DataNotFoundException("존재하지 않는 사용자 정보: username");
        }
        return result.get();
    }

    @Transactional
    public Member updateMember(UpdateMemberDto dto) {
        Member existingMember = findByUsername(dto.getUsername());
        if (dto.getPassword() != null){
            existingMember.setPassword(passwordEncoder.encode(existingMember.getPassword()));
        }
        if (dto.getBirthDate() != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            existingMember.setBirthDate(LocalDate.parse(dto.getBirthDate(), formatter));
        }
        if (dto.getAddress() != null){
            existingMember.setAddress(dto.getAddress());
        }
        if (dto.getAddressDetail() != null){
            existingMember.setAddressDetail(dto.getAddressDetail());
        }
        if (dto.getNickName() != null){
            existingMember.setNickname(dto.getNickName());
        }
        memberRepository.save(existingMember);
        return new Member();
    }

//    public Optional<Member> findByEmail(String memberEmail){
//        return memberRepository.findByEmail(memberEmail);
//    }
}
