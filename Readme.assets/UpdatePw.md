# UpdatePw 비밀번호 찾기 / 회원정보 중 비밀번호 수정
## I. DTO 구현
### 1. UpdatePwDTO
비밀번호 찾기 시 필요한 회원 정보를 포함한 DTO 클래스입니다.

```java
@Getter
public class UpdatePwDTO {
    private String email;
    private String password;
    private String newPassword;
}
```

### 2. MailDTO
비밀번호 찾기 시 임시 비밀번호를 발급할 메일 DTO입니다.

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MailDTO {
    private String email;
    private String title;
    private String message;
}
```

## II. 비밀번호 찾기 Service 구현
MemberServiceImpl
비밀번호 찾기 서비스 클래스입니다.
전송할 메일 정보를 생성하고, 임시 비밀번호 구문을 만들어 메일로 전송합니다.
사용자가 입력한 임시 비밀번호와 전송한 임시 비밀번호가 일치할 시 새로운 비밀번호로 변경할 수 있습니다.
만약 사용자가 입력한 이메일과 일치하는 유저 정보가 존재하지 않거나 <br>
전송한 임시 비밀번호와 입력한 임시 비밀번호가 일치하지 않을 시 예외를 던집니다.

```java
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberServiceImpl implements MemberService {
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
}
```

## III. 비밀번호 Controller 구현
### MemberController
비밀번호 찾기와 관련된 API 엔드포인트를 제공합니다.
임시 비밀번호 메일 전송과 비밀번호 변경 기능을 포함합니다.

```java
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    // 임시 비밀번호 메일 전송
    @PostMapping("/sendEmail")
    public ResponseEntity<Object> sendEmail(@RequestParam("email") String email) {
        MailDTO dto = memberService.createMailAndChangePw(email);
        memberService.mailSend(dto);
        return new ResponseEntity<>("메일 전송 완료", HttpStatus.OK);
    }

    // 비밀번호 변경 
    @PutMapping("/updatePw")
    public ResponseEntity<Object> updatePw(@RequestBody UpdatePwDTO updatePwDTO) {
        memberService.updatePassword(updatePwDTO.getEmail(), updatePwDTO.getPassword(), updatePwDTO.getNewPassword());
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }
}
```

## IIII. 예외 처리
### MemberException
회원 관련 예외 처리를 위한 커스텀 예외 클래스입니다.

```java
public class MemberException extends GlobalException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

### ErrorCode
예외 처리에 사용할 오류 코드를 정의한 enum 클래스입니다.

```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
```
