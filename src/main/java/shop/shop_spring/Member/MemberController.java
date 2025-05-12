package shop.shop_spring.Member;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Dto.ApiResponse;
import shop.shop_spring.Email.EmailService;

import java.io.UnsupportedEncodingException;
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
        memberService.join(form);
        return "";
    }

    @GetMapping("/login")
    public String login() {
        return "/members/login";
    }

    // 인증 번호 전송
    @PostMapping("/members/verify-email")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendEmail(@RequestBody Map<String, String> data) throws MessagingException, UnsupportedEncodingException {
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
    public ResponseEntity<Map<String, String>> validateEmail(@RequestBody Map<String, String> data) {
        Map<String, String> responseBody = new HashMap<>();
        memberService.validateAuthenticationCode(data.get("email"), data.get("code"));
        System.out.println("인증완료");
        ApiResponse<Map<String, String>> successResponse = ApiResponse.
                success("인증 번호 발송 성공", responseBody);
        return ResponseEntity.status(200).body(responseBody);
    }
}
