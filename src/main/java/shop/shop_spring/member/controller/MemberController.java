package shop.shop_spring.member.controller;

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
import shop.shop_spring.Dto.CustomApiResponse;
import shop.shop_spring.member.Dto.MemberCreationRequest;
import shop.shop_spring.member.Dto.MemberUpdateRequest;
import shop.shop_spring.member.MemberForm;
import shop.shop_spring.member.domain.Member;
import shop.shop_spring.member.service.MemberServiceImpl;
import shop.shop_spring.product.Dto.ProductSearchCondition;
import shop.shop_spring.product.Dto.ProductUpdateRequest;
import shop.shop_spring.product.service.ProductService;
import shop.shop_spring.product.domain.Product;
import shop.shop_spring.security.auth.AuthService;
import shop.shop_spring.security.model.MyUser;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberServiceImpl memberService;
    private final ProductService productService;
    private final AuthService authService;

    // 회원 가입 페이지 요청
    @GetMapping("/register")
    public String createForm(){
        return "members/register";
    }

    /**
     * 회원가입 요청
     */
    @PostMapping
    public String signup(@ModelAttribute MemberForm form){
        MemberCreationRequest memberCreationRequest = formToMemberCreationReqeust(form);
        memberService.createMember(memberCreationRequest);
        return "redirect:/members/login";
    }

    @GetMapping("/login")
    public String login() {
        return "members/login";
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

        return "members/my-page/my-page";
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

        return "members/my-page/profile";
    }

    @GetMapping("/my-page/products")
    public String showProducts(Authentication auth, Model model){
        MyUser myUser = (MyUser) auth.getPrincipal();

        ProductSearchCondition searchCondition = new ProductSearchCondition();
        searchCondition.setSellerUsername(myUser.getUsername());

        List<Product> products = productService.searchProducts(searchCondition);

        model.addAttribute("username", myUser.getUsername());
        model.addAttribute("products", products);

        return "members/my-page/products";
    }

    @GetMapping("/my-page/products/{id}")
    String showDetail(@PathVariable Long id, Model model){
        Product product = productService.findById(id);

        model.addAttribute("product", product);

        return "members/my-page/edit-product";
    }

    @PutMapping("/my-page/products/{id}")
    ResponseEntity editDetail(@PathVariable Long id, @RequestBody ProductUpdateRequest updateRequest, Authentication auth){
        MyUser user = (MyUser) auth.getPrincipal();
;
        productService.updateProduct(user.getUsername(), id, updateRequest);

        CustomApiResponse<Void> response = CustomApiResponse.successNoData("상품 정보 수정 완료");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/my-page/products/{id}")
    ResponseEntity deleteProduct(@PathVariable Long id, Authentication auth){
        MyUser user = (MyUser) auth.getPrincipal();

        productService.deleteProduct(user.getUsername(), id);

        CustomApiResponse<Void> response = CustomApiResponse.successNoData("상품 삭제 완료");
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PutMapping("/my-page/profile")
    public ResponseEntity<CustomApiResponse<Void>> modifyProfile(@RequestBody MemberUpdateRequest dto){
        // validateMember(updateMemberDtoToMember(dto)) 검증 한번 하는게 좋을 듯
        memberService.updateMember(updateMemberDtoToMember(dto));
        
        CustomApiResponse<Void> successResponse = CustomApiResponse.successNoData("회원 정보 수정 완료");
        
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }


    private Member updateMemberDtoToMember(MemberUpdateRequest dto){
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

        Map<String, String> responseData = CustomApiResponse.createResponseData("username", data.get("username"));
        CustomApiResponse<Map<String, String>> successResponse =
                CustomApiResponse.success("본인 인증 완료", responseData);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity updatePassword(@RequestBody Map<String, String> data){

        memberService.updatePassword(data.get("username"), data.get("newPassword"));

        CustomApiResponse<Void> response = CustomApiResponse.successNoData("비밀번호 변경 완료");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 인증 번호 전송
    @PostMapping("/verify-email")
    public ResponseEntity<CustomApiResponse<Map<String, String>>> sendEmail(@RequestBody Map<String, String> data) throws MessagingException, UnsupportedEncodingException {
        // 인증 번호 생성 및 메일 전송
        memberService.sendAuthenticationCode(data.get("email"));

        // 성공 시 응답 데이터 준비
        Map<String, String> responseData = CustomApiResponse.createResponseData("email", data.get("email"));
        CustomApiResponse<Map<String, String>> successResponse =
                CustomApiResponse.success("인증 번호 발송 성공", responseData);

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    //이메일 인증
    @PostMapping("/validate-email")
    public ResponseEntity<CustomApiResponse<Map<String, String>>> validateEmail(@RequestBody Map<String, String> data) {
        memberService.validateAuthenticationCode(data.get("email"), data.get("code"));

        Map<String, String> responseData = CustomApiResponse.createResponseData("email", data.get("email"));
        CustomApiResponse<Map<String, String>> successResponse =
                CustomApiResponse.success("이메일 인증 성공", responseData);

        return ResponseEntity.status(200).body(successResponse);
    }

    private MemberCreationRequest formToMemberCreationReqeust (MemberForm form) {
        MemberCreationRequest memberCreationRequest = new MemberCreationRequest();
        memberCreationRequest.setUsername(form.getEmail());
        memberCreationRequest.setPassword(form.getPassword());
        memberCreationRequest.setName(form.getName());
        memberCreationRequest.setAddress(form.getAddress());
        memberCreationRequest.setAddressDetail(form.getAddressDetail());
        memberCreationRequest.setBirthDate(form.getBirthDate());
        memberCreationRequest.setNickname(form.getNickname());
        return memberCreationRequest;
    }

}
