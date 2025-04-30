package shop.shop_spring.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/member/new")
    String createForm(){
        return "member/register";
    }

    @PostMapping("/member")
    String signup(@ModelAttribute MemberForm form){
        memberService.join(form);
        return "";
    }

    @GetMapping("/login")
    String login() {
        return "/member/login";
    }

}
