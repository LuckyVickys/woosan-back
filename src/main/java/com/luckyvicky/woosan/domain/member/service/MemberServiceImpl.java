package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.dto.DeleteRequestDTO;
import com.luckyvicky.woosan.domain.member.dto.MailDTO;
import com.luckyvicky.woosan.domain.member.dto.MemberInfoDTO;
import com.luckyvicky.woosan.domain.member.entity.JoinCode;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.entity.SocialType;
import com.luckyvicky.woosan.domain.member.mapper.MemberMapper;
import com.luckyvicky.woosan.domain.member.repository.redis.JoinCodeRepository;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;
    private final MemberMapper mapper;
    private final FileImgService fileImgService;
    private final JoinCodeRepository joinCodeRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 이메일 중복 체크
    @Override
    public Boolean existEmail(String email) {
        if(memberRepository.existsByEmail(email) == true) {
            throw new MemberException(ErrorCode.EMAIL_DUPLICATE);
        } else {
            return memberRepository.existsByEmail(email);
        }
    }

    // 닉네임 중복 체크
    @Override
    public Boolean existNickname(String nickname) {
        if(memberRepository.existsByNickname(nickname) == true) {
            throw new MemberException(ErrorCode.NICKNAME_DUPLICATE);
        } else {
            return memberRepository.existsByEmail(nickname);
        }
    }

    // 회원가입
    @Override
    public Member addMember(Member member) {
        if(existEmail(member.getEmail()) == true) {
            throw new MemberException(ErrorCode.EMAIL_DUPLICATE);
        } else if(existNickname(member.getNickname()) == true) {
            throw new MemberException(ErrorCode.NICKNAME_DUPLICATE);
        }

        member = Member.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .password(bCryptPasswordEncoder.encode(member.getPassword()))
                .point(0)
                .nextPoint(100)
                .memberType(member.getEmail().equals("woosan@bitcamp.com") ? MemberType.ADMIN : MemberType.USER)
                .level(member.getEmail().equals("woosan@bitcamp.com") ? null : MemberType.Level.LEVEL_1)
                .isActive(true)
                .socialType(SocialType.NORMAL)
                .build();

        return memberRepository.save(member);
    }

    // 회원 탈퇴
    @Override
    public String deleteMember(DeleteRequestDTO deleteRequestDTO) {
        String email = deleteRequestDTO.getEmail();
        String password = deleteRequestDTO.getPassword();
        if(!memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Member member = memberRepository.findByEmail(email);
        if(!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(ErrorCode.PW_NOT_FOUND);
        }
        memberRepository.save(member.changeIsActive());
        return "회원 탈퇴 성공";
    }

    /**
     * 회원가입 코드 발급 및 확인
     */
    // 회원가입 메일 내용 생성 및 코드 redis에 저장
    @Override
    public MailDTO createJoinCodeMail(String email) {
        try {
            String str = getTempPassword();
            JoinCode code = new JoinCode(str, email);
            joinCodeRepository.save(code);
            MailDTO dto = new MailDTO(email,
                    "Woosan 회원가입 확인 이메일입니다.",
                    "안녕하세요. Woosan 회원가입 확인 관련 이메일 입니다." + " 회원님의 가입 코드는 "
                            + str + " 입니다. " + "회원가입을 마쳐주세요.");
            return dto;
        } catch (Exception e) {
            throw new MemberException(ErrorCode.SERVER_ERROR);
        }
    }

    // 회원가입 코드 redis에 존재 확인
    @Override
    public Boolean checkJoinCode(String joinCode) {
        try {
            Boolean isExist = joinCodeRepository.existsById(joinCode);
            return isExist;
        } catch (Exception e) {
            throw new MemberException(ErrorCode.SERVER_ERROR);
        }
    }


    /**
     * 임시 비밀번호 발급 및 비밀번호 변경
     */
    // 메일 내용 생성 및 임시 비밀번호로 회원 비밀번호 변경
    @Override
    public MailDTO createMailAndChangePw(String email) {
        try {
            String str = getTempPassword();
            MailDTO dto = new MailDTO(email,
                    "Woosan 임시비밀번호 안내 이메일입니다.",
                    "안녕하세요. Woosan 임시비밀번호 안내 관련 이메일 입니다." + " 회원님의 임시 비밀번호는 "
                            + str + " 입니다." + "로그인 후에 비밀번호를 변경을 해주세요.");
            updateTempPw(str,email);
            return dto;
        } catch (Exception e) {
            throw new MemberException(ErrorCode.SERVER_ERROR);
        }
    }

    // 임시 비밀번호로 업데이트
    public void updateTempPw(String str, String email) throws Exception {
        Member member = memberRepository.findByEmail(email);
        if(member != null) {
            member.changePassword(bCryptPasswordEncoder.encode(str));
            memberRepository.save(member);
        } else {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    // 랜덤함수로 임시 비밀번호 구문 만들기
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
    public void mailSend(MailDTO mailDTO) {
        try {
            System.out.println("메일 전송 완료");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mailDTO.getEmail());
            message.setSubject(mailDTO.getTitle());
            message.setText(mailDTO.getMessage());
            message.setFrom(fromEmail);
            message.setReplyTo(fromEmail);
            System.out.println("message" + message);
            mailSender.send(message);
        } catch (Exception e) {
            throw new MemberException(ErrorCode.SERVER_ERROR);
        }
    }

    // 비밀번호 변경
    @Override
    public void updatePassword(String email, String password, String newPassword){
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        } else if(!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(ErrorCode.PW_NOT_FOUND);
        }

        try {
            member.changePassword(bCryptPasswordEncoder.encode(newPassword));
            memberRepository.save(member);
        } catch (Exception e) {
            throw new MemberException(ErrorCode.SERVER_ERROR);
        }
    }

    // 로그인 한 멤버 정보
    @Override
    public MemberInfoDTO getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        MemberInfoDTO dto = mapper.memberToMemberInfoDTO(member);

        List<String> profile = fileImgService.findFiles("member", member.getId());

        if(profile != null){
            dto.setProfile(profile);
        }

        return dto;
    }
}
