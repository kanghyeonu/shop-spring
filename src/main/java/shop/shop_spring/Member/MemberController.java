package shop.shop_spring.Member;

import org.springframework.ui.Model;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.shop_spring.Dto.ApiResponse;
import shop.shop_spring.Dto.UpdateMemberDto;
import shop.shop_spring.Member.domain.Member;
import shop.shop_spring.Member.domain.enums.Role;
import shop.shop_spring.Security.AuthService;
import shop.shop_spring.Security.MyUser;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;


    /**
     * 회원 가입 페이지 요청
     * @return
     */
    @GetMapping("/register")
    public String createForm(){
        return "members/register";
    }

    /**
     * 회원가입
     * @param form
     * @return 로그인 페이지
     */
    @PostMapping
    public String signup(@ModelAttribute MemberForm form){
        Member member = formToMember(form);
        memberService.join(member);
        return "redirect:/members/login";
    }

    @GetMapping("/login")
    public String login() {
        return "/members/login";
    }

    /**
     * 로그인 수행
     * @param data
     * @param httpServletResponse
     * @return 로그인에 대한 jwt
     */
    @PostMapping("/login")
    @ResponseBody
    public String doLogin(@RequestBody Map<String, String> data,
                          HttpServletResponse httpServletResponse) {
        String jwt = authService.login(data.get("username"), data.get("password"));

        addJwtCookieToResponse(httpServletResponse, jwt);

        return jwt;
    }

    /**
     * 쿠키 추가
     */
    private void addJwtCookieToResponse(HttpServletResponse httpServletResponse, String jwt){
        // 쿠키에 jwt 저장
        var cookie = new Cookie("jwt", jwt);
        cookie.setMaxAge(60 * 5);
        cookie.setHttpOnly(true); // js 등에서 접근 x
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    @GetMapping("/my-page")
    public String showMyPage(Authentication auth){
        MyUser user = (MyUser) auth.getPrincipal();

        return "members/my-page";
    }

    @GetMapping("/my-page/profile")
    public String showProfile(Authentication auth, Model model){
        MyUser myUser = (MyUser) auth.getPrincipal();
        Member member = memberService.findByUsername(myUser.getUsername());

        model.addAttribute("username", member.getUsername());
        model.addAttribute("name", member.getName());
        model.addAttribute("birthDate", member.getBirthDate());
        model.addAttribute("address", member.getAddress());
        model.addAttribute("addressDetail", member.getAddressDetail());
        model.addAttribute("nickName", member.getNickname());

        return "members/profile";
    }

    @PutMapping("/my-page/profile")
    public ResponseEntity<ApiResponse<Void>> modifyProfile(@RequestBody UpdateMemberDto dto){
        // validateMember(updateMemberDtoToMember(dto)) 검증 한번 하는게 좋을 듯
        memberService.updateMember(updateMemberDtoToMember(dto));
        
        ApiResponse<Void> successResponse = ApiResponse.successNoData("회원 정보 수정 완료");
        
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }
    
    private Member updateMemberDtoToMember(UpdateMemberDto dto){
        Member member = new Member();
        member.setUsername(dto.getUsername());
        member.setPassword(dto.getPassword());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(dto.getBirthDate(), formatter);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식");
        }
        member.setBirthDate(localDate);
        member.setAddress(dto.getAddress());
        member.setAddressDetail(dto.getAddressDetail());
        member.setNickname(dto.getNickName());
        return member;
    }

    @GetMapping("/password-reset")
    public String showPasswordReset(){
        return "members/password-reset";
    }

    @PostMapping("/password-reset")
    @ResponseBody
    public ResponseEntity resetPassword(@RequestBody Map<String, String> data){
        String username = data.get("username");
        String name = data.get("name");
        String brithDate = data.get("birthDate");

        memberService.validateUserInformation(username, name, brithDate);

        Map<String, String> responseData = createResponseData("username", data.get("username"));
        ApiResponse<Map<String, String>> successResponse =
                ApiResponse.success("본인 인증 완료", responseData);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity updatePassword(@RequestBody Map<String, String> data){

        memberService.updatePassword(data.get("username"), data.get("newPassword"));

        ApiResponse<Void> response = ApiResponse.successNoData("비밀번호 변경 완료");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 인증 번호 전송
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendEmail(@RequestBody Map<String, String> data) throws MessagingException, UnsupportedEncodingException {
        // 인증 번호 생성 및 메일 전송
        memberService.sendAuthenticationCode(data.get("email"));

        // 성공 시 응답 데이터 준비
        Map<String, String> responseData = createResponseData("email", data.get("email"));
        ApiResponse<Map<String, String>> successResponse =
                ApiResponse.success("인증 번호 발송 성공", responseData);

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    //이메일 인증
    @PostMapping("/validate-email")
    public ResponseEntity<ApiResponse<Map<String, String>>> validateEmail(@RequestBody Map<String, String> data) {
        memberService.validateAuthenticationCode(data.get("email"), data.get("code"));

        Map<String, String> responseData = createResponseData("email", data.get("email"));
        ApiResponse<Map<String, String>> successResponse =
                ApiResponse.success("이메일 인증 성공", responseData);

        return ResponseEntity.status(200).body(successResponse);
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

    private Map<String, String> createResponseData(String key, String message){
        Map<String, String> responseData = new HashMap<>();
        responseData.put(key, message);;
        return responseData;
    }

    private Map<String, ?> createResponseData(List<String> key, List<?> data){
        if (key.size() != data.size()) {
            throw new IllegalArgumentException("입력된 key와 data의 길이가 다름");
        }
        Map<String, Object> responseData = new HashMap<>();
        for (int i = 0; i < key.size(); i++){
            responseData.put(key.get(i), data.get(i));
        }
        return responseData;
    }
}
