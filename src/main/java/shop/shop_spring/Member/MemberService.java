package shop.shop_spring.Member;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public void join(MemberForm form){
        Member member = modelMapper.map(form, Member.class);
        System.out.println(member.getName());
        System.out.println(member.getBirthDate());
    }
}
