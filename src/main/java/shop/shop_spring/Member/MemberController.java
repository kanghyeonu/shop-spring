package shop.shop_spring.Member;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Domain.Member;
import shop.shop_spring.Dto.ApiResponse;
import shop.shop_spring.Email.EmailService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(){
        return "members/register";
    }

    @PostMapping("/members")
    public String signup(@ModelAttribute MemberForm form){
        Member member = formToMember(form);
        memberService.join(member);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "/members/login";
    }

    // 인증 번호 전송
    @PostMapping("/members/verify-email")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendEmail(@RequestBody Map<String, String> data) throws MessagingException, UnsupportedEncodingException {
        // 중복 회원 체크
        memberService.validateDuplicateMember(data.get("email"));
        
        memberService.sendAuthenticationCode(data.get("email"));

        // 성공 시 응답 데이터 준비
        Map<String, String> responseData = new HashMap<>();
        responseData.put("email", data.get("email"));

        // 성공 응답 객체 생성 및 반환
        ApiResponse<Map<String, String>> successResponse = ApiResponse.
                success("인증 번호 발송 성공", responseData);

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    //이메일 인증
    @PostMapping("/members/validate-email")
    public ResponseEntity<ApiResponse<Map<String, String>>> validateEmail(@RequestBody Map<String, String> data) {
        memberService.validateAuthenticationCode(data.get("email"), data.get("code"));

        Map<String, String> responseData = new HashMap<>();
        responseData.put("email", data.get("email"));

        ApiResponse<Map<String, String>> successResponse = ApiResponse.
                success("인증 성공", responseData);

        return ResponseEntity.status(200).body(successResponse);
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

}
