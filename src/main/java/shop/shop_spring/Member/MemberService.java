package shop.shop_spring.Member;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.shop_spring.Email.Dto.EmailDto;
import shop.shop_spring.Email.EmailServiceImpl;
import shop.shop_spring.Exception.DataNotFoundException;
import shop.shop_spring.Member.Dto.MemberCreationRequest;
import shop.shop_spring.Member.domain.Member;
import shop.shop_spring.Member.domain.enums.Role;
import shop.shop_spring.Redis.RedisEmailAuthentication;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    public Long createMember(MemberCreationRequest request){
        Member member = MemberCreationRequestToMember(request);
        validateMember(member);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
        return member.getId();
    }

    private Member MemberCreationRequestToMember(MemberCreationRequest request){
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(request.getPassword());
        member.setName(request.getName());
        member.setAddress(request.getAddress());
        member.setAddressDetail(request.getAddressDetail());

        if (request.getBirthDate() == null || request.getBirthDate().trim().isEmpty()){
            throw new IllegalArgumentException("생년월일이 비었음");
        }
        String localDateStr = request.getBirthDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(localDateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("입력된 '" + localDateStr + "'는 유효한 YYYYMMDD 날짜 형식이 아니거나 유효한 날짜가 아닙니다.", e);
        }
        member.setBirthDate(localDate);
        member.setNickname(request.getNickname());
        member.setRole(Role.ROLE_USER);

        return member;
    }

    private void validateMember(Member member) {
        /**
         *  이메일: 고유
         *  비밀번호: FE에서 판단 될지도?
         *  주소 & 상세 주소:
         *  생일
         */
        validateDuplicateMember(member.getUsername());
    }

    private void validateDuplicateMember(String username) {
        memberRepository.findByUsername(username)
                .ifPresent(m -> {
                    throw new IllegalArgumentException("이미 존재하는 회원");
                });
    }

    private Member formToMember (MemberForm form) {
        Member member = new Member();
        member.setUsername(form.getEmail());
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
        member.setRole(Role.ROLE_USER);

        return member;
    }


    public void sendAuthenticationCode(String email) throws MessagingException, UnsupportedEncodingException {
        validateDuplicateMember(email);
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
    public void updateMember(Member member) {
        Member existingMember = findByUsername(member.getUsername());
        if (member.getPassword() != null){
            existingMember.setPassword(passwordEncoder.encode(member.getPassword()));
        }
        if (member.getBirthDate() != null){
            existingMember.setBirthDate(member.getBirthDate());
        }
        if (member.getAddress() != null){
            existingMember.setAddress(member.getAddress());
        }
        if (member.getAddressDetail() != null){
            existingMember.setAddressDetail(member.getAddressDetail());
        }
        if (member.getNickname() != null){
            existingMember.setNickname(member.getNickname());
        }
        memberRepository.save(existingMember);
    }

    public void validateUserInformation(String username, String name, String birthDate) {
        Member member = findByUsername(username);

        if (!member.getName().equals(name)){
            System.out.println(member.getName() + name);
            throw new IllegalArgumentException("잘못된 회원 정보");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String birthDateFromDb = member.getBirthDate().format(formatter);
        if (!birthDateFromDb.equals(birthDate)){
            throw new IllegalArgumentException("잘못된 회원 정보");
        }

    }

    @Transactional
    public void updatePassword(String username, String newPassword) {
        Member member = findByUsername(username);
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

//    public Optional<Member> findByEmail(String memberEmail){
//        return memberRepository.findByEmail(memberEmail);
//    }
}
