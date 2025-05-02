package shop.shop_spring.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import shop.shop_spring.Email.EmailService;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

    @GetMapping("/members/new")
    String createForm(){
        return "members/register";
    }

    @PostMapping("/members")
    String signup(@ModelAttribute MemberForm form){
        memberService.join(form);
        return "";
    }

    @GetMapping("/login")
    String login() {
        return "/members/login";
    }

}
