package shop.shop_spring.Member;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public void join(MemberForm form){
        Member member = formToMember(form);
        validateMember(member);
        memberRepository.save(member);
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


    private void validateMember(Member member) {
        /**
         *  이메일
         *  비밀번호
         *  주소 & 상세 주소
         *  생일
         */
    }


}
