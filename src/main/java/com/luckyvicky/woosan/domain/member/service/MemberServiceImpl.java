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
import com.luckyvicky.woosan.global.annotation.SlaveDBRequest;
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
import java.util.Random;

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
    @SlaveDBRequest
    @Override
    public Boolean existEmail(String email) {
        if(memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.EMAIL_DUPLICATE);
        }
        return false;
    }

    // 닉네임 중복 체크
    @SlaveDBRequest
    @Override
    public Boolean existNickname(String nickname) {
        if(memberRepository.existsByNickname(nickname)) {
            throw new MemberException(ErrorCode.NICKNAME_DUPLICATE);
        }
        return false;
    }

    // 회원가입
    @Override
    public Member addMember(Member member) {
        existEmail(member.getEmail());
        existNickname(member.getNickname());

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
        Member member = memberRepository.findByEmail(email);
        if(member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
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
            String str = generateRandomCode();
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
            return joinCodeRepository.existsById(joinCode);
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
            String tempPassword = generateRandomCode(); // 임시 비밀번호 생성
            MailDTO mailDTO = new MailDTO(email,
                    "Woosan 임시비밀번호 안내 이메일입니다.",
                    "안녕하세요. Woosan 임시비밀번호 안내 관련 이메일입니다. 회원님의 임시 비밀번호는 "
                            + tempPassword + " 입니다. 로그인 후 비밀번호를 변경해 주세요.");
            updateTempPassword(tempPassword, email); // 임시 비밀번호로 업데이트
            return mailDTO;
        } catch (Exception e) {
            throw new MemberException(ErrorCode.SERVER_ERROR);
        }
    }

    // 임시 비밀번호로 업데이트
    private void updateTempPassword(String tempPassword, String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        member.changePassword(bCryptPasswordEncoder.encode(tempPassword));
        memberRepository.save(member);
    }

    // 랜덤함수로 임시 비밀번호 구문 만들기
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(36); // [0, 36) 범위의 랜덤 값
            code.append(index < 10 ? (char) ('0' + index) : (char) ('A' + index - 10));
        }
        return code.toString();
    }

     // 메일 전송
    @Override
    public void mailSend(MailDTO mailDTO) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mailDTO.getEmail());
            message.setSubject(mailDTO.getTitle());
            message.setText(mailDTO.getMessage());
            message.setFrom(fromEmail);
            message.setReplyTo(fromEmail);
            mailSender.send(message);
        } catch (Exception e) {
            throw new MemberException(ErrorCode.SERVER_ERROR);
        }
    }

    // 비밀번호 변경
    public void updatePassword(String email, String currentPassword, String newPassword) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (!bCryptPasswordEncoder.matches(currentPassword, member.getPassword())) {
            throw new MemberException(ErrorCode.PW_NOT_FOUND);
        }
        member.changePassword(bCryptPasswordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    // 로그인 한 멤버 정보
    @SlaveDBRequest
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
