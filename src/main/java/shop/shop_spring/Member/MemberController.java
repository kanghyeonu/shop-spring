package shop.shop_spring.Member;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Email.EmailDto;
import shop.shop_spring.Email.EmailService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

    @GetMapping("/members/new")
    public String createForm(){
        return "members/register";
    }

    @PostMapping("/members")
    public String signup(@ModelAttribute MemberForm form){
        memberService.join(form);
        return "";
    }

    @GetMapping("/login")
    public String login() {
        return "/members/login";
    }

    // 인증 번호 전송
    @PostMapping("/members/verify-email")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> data) {
        Map<String, String> responseBody = new HashMap<>();

        try{
            memberService.sendAuthenticationCode(data.get("email"));
            responseBody.put("message", "인증 번호 발송 성공");
            responseBody.put("email", data.get("email"));
            return ResponseEntity.status(200).body(responseBody);

        } catch (MessagingException e) {
            // MessagingException 예외 처리
            responseBody.put("message", "이메일 전송 중 시스템 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(responseBody);

        } catch (UnsupportedEncodingException e) {
            // UnsupportedEncodingException 예외 처리
            responseBody.put("message", "이메일 데이터 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(responseBody);

        } catch (Exception e) {
            // 혹시 모를 다른 예외 처리
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            responseBody.put("message", "서버 내부 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(responseBody);
        }
    }
//
//    //이메일 인증
//    @PostMapping("/verifyEmail")
//    public GlobalResponse<MemberEmailVerifyResponseDto> verifyEmail(@RequestBody MemberEmailVerifyRequestDto requestDto) {
//        boolean isVerified = memberService.verifyCode(requestDto.getEmail(), requestDto.getVerificationCode());
//        MemberEmailVerifyResponseDto responseDto = new MemberEmailVerifyResponseDto();
//        responseDto.setVerified(isVerified);
//        responseDto.setMessage(isVerified ? "Email verified successfully." : "Invalid or expired verification code.");
//        if(isVerified) return GlobalResponse.of("200", "인증 완료", responseDto);
//        else return GlobalResponse.of("200", "인증 실패", responseDto);
//    }
}
