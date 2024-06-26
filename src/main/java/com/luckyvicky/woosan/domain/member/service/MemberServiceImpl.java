package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.LoginReqDTO;
import com.luckyvicky.woosan.domain.member.dto.MailDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.entity.SocialType;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 이메일 중복 체크
    @Override
    public Boolean existEmail(String email) throws Exception {
        return memberRepository.existsByEmail(email);
    }

    // 닉네임 중복 체크
    @Override
    public Boolean existNickname(String nickname) throws Exception {
        return memberRepository.existsByNickname(nickname);
    }

    // 회원가입
    @Override
    public Member addMember(Member member) throws Exception {
        if(existEmail(member.getEmail()) == true) {
            throw new Exception("중복된 이메일입니다.");
        } else if(existNickname(member.getNickname()) == true) {
            throw new Exception("중복된 닉네임입니다.");
        }

        member = Member.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
//                .password(bCryptPasswordEncoder.encode(member.getPassword()))
                .password(member.getPassword())
                .point(0L)
                .memberType(member.getEmail().equals("woosan@bitcamp.com") ? MemberType.ADMIN : MemberType.USER)
                .level(member.getEmail().equals("woosan@bitcamp.com") ? null : MemberType.Level.LEVEL_1)
                .isActive(true)
                .socialType(SocialType.NORMAL)
                .build();

        return memberRepository.save(member);
    }

    // 세션 로그인(스프링시큐리티, jwt토큰 적용 전)
    @Override
    public Boolean login(LoginReqDTO loginReqDTO) throws Exception {
        String email = loginReqDTO.getEmail();
        String password = loginReqDTO.getPassword();
        boolean isCorrectEmail = memberRepository.existsByEmail(email);
        boolean isCorrectPw = memberRepository.existsByEmailAndPassword(email, password);

        if(!isCorrectEmail) {
            throw new Exception("존재하지 않는 이메일입니다.");
        } else if(!isCorrectPw) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        Long memberId = memberRepository.findByEmail(email).getId();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);   // 세션이 존재하지 않는다면 세션 생성
        session.setAttribute("memberId", memberId);
        return true;
    }

    /**
     * 임시 비밀번호 발급 및 비밀번호 변경 관련 코드들
     */
    //메일 내용 생성 및 임시 비밀번호로 회원 비밀번호 변경
    @Override
    public MailDTO createMailAndChangePw(String email) throws Exception {
        String str = getTempPassword();
        MailDTO dto = new MailDTO(email,
                "Woosan 임시비밀번호 안내 이메일입니다.",
                "안녕하세요. Woosan 임시비밀번호 안내 관련 이메일 입니다." + " 회원님의 임시 비밀번호는 "
                        + str + " 입니다." + "로그인 후에 비밀번호를 변경을 해주세요");
        updateTempPw(str,email);
        return dto;
    }

    // 임시 비밀번호로 업데이트
    @Override
    public void updateTempPw(String str, String email) throws Exception {
        Member member = memberRepository.findByEmail(email);
        if(member != null) {
            member.changePassword(str);
//        member.changePassword(bCryptPasswordEncoder.encode(str));
            memberRepository.save(member);
        } else {
            throw new Exception("회원정보가 일치하지 않습니다.");
        }
    }

    // 랜덤함수로 임시 비밀번호 구문 만들기
    @Override
    public String getTempPassword() throws Exception {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

     // 메일 전송
    @Override
    public void mailSend(MailDTO mailDTO) throws Exception {
        System.out.println("메일 전송 완료");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDTO.getEmail());
        message.setSubject(mailDTO.getTitle());
        message.setText(mailDTO.getMessage());
        message.setFrom(fromEmail);
        message.setReplyTo(fromEmail);
        System.out.println("message" + message);
        mailSender.send(message);
    }

    // 비밀번호 변경
    @Override
    public void updatePassword(String email, String password, String newPassword) throws Exception {
        Member member = memberRepository.findByEmail(email);

        System.out.println("memberPassword: " + member.getPassword());
        System.out.println("parameterPassword: " + password);

        if(member == null) {
            throw new Exception("존재하지 않는 이메일입니다.");
        } else if(!member.getPassword().equals(password)) {
            throw new Exception("비밀번호가 틀립니다.");
        }

        member.changePassword(newPassword);
//                member.changePassword(bCryptPasswordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

}
