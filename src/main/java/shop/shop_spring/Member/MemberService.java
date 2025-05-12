package shop.shop_spring.Member;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.shop_spring.Dto.EmailDto;
import shop.shop_spring.Email.EmailServiceImpl;
import shop.shop_spring.Exception.DataNotFoundException;
import shop.shop_spring.Redis.RedisEmailAuthentication;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final EmailServiceImpl emailService;
    private final RedisEmailAuthentication redisEmailAuthentication;

    public void join(MemberForm form){
        Member member = formToMember(form);
        validateMember(member);
        memberRepository.save(member);
    }

    private void validateMember(Member member) {
        /**
         *  이메일
         *  비밀번호
         *  주소 & 상세 주소
         *  생일
         */
    }

    private Member formToMember (MemberForm form) {
        Member member = new Member();
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        member.setAddress(form.getAddress());
        member.setAddressDetail(form.getAddressDetail());

        String localDateStr = form.getBirthDate();
        if (localDateStr == null || localDateStr.trim().isEmpty()){
            throw new IllegalArgumentException("생년월일이 비었음");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(localDateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("입력된 '" + localDateStr + "'는 유효한 YYYYMMDD 날짜 형식이 아니거나 유효한 날짜가 아닙니다.", e);
        }
        member.setBirthDate(localDate);
        member.setNickname(form.getNickname());

        return member;
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
}
