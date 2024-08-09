# SignUp 회원가입

## I. Entity, Enum 구현

### 1. Member Entity
회원 테이블과 매핑되는 Member 엔티티 클래스입니다.<br>
회원가입 시 필요한 정보와 권한 관리 등을 포함합니다.

```java
@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private int point;
    private int nextPoint;
    private boolean isActive;

    // 회원가입용 빌더 패턴
    @Builder
    public Member(String email, String nickname, String password, MemberType memberType, SocialType socialType) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.memberType = memberType;
        this.socialType = socialType;
        this.point = 0;
        this.nextPoint = 100;
        this.isActive = true;
    }

    // 비밀번호 변경 메소드
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    // 회원 탈퇴 메소드
    public Member changeIsActive() {
        this.isActive = false;
        return this;
    }
}
```

### 2. MemberType Enum
회원의 권한을 구분하기 위한 enum 클래스입니다.

```java
public enum MemberType {
    ADMIN, USER;
    
    public enum Level {
        LEVEL_1, LEVEL_2, LEVEL_3;
    }
}
```
### 3. SocialType Enum
카카오 로그인 여부를 확인하기 위한 enum 클래스입니다.

```java
public enum SocialType {
    NORMAL, KAKAO;
}
```
<br>

## II. 회원가입 Service 구현
### MemberServiceImpl
회원가입 서비스 클래스입니다.<br>
회원 정보의 유효성을 검사하고, 중복된 이메일이나 닉네임이 있는 경우 예외를 던집니다.<br>
또한 비밀번호는 **BCryptPasswordEncoder**를 사용하여 암호화됩니다.<br>
회원가입 코드가 이메일로 발급되고, 일치 여부를 확인하여 발급된 코드와 회원이 입력한 코드가 일치하지 않을 시 예외를 던집니다.

```java
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;

    @Override
    public Member addMember(Member member) {
        existEmail(member.getEmail());
        existNickname(member.getNickname());

        member = Member.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .password(bCryptPasswordEncoder.encode(member.getPassword()))
                .memberType(member.getEmail().equals("admin@woosan.com") ? MemberType.ADMIN : MemberType.USER)
                .socialType(SocialType.NORMAL)
                .build();

        return memberRepository.save(member);
    }

    // 이메일 중복 체크
    @Override
    public Boolean existEmail(String email) {
        if(memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.EMAIL_DUPLICATE);
        }
        return false;
    }

    // 닉네임 중복 체크
    @Override
    public Boolean existNickname(String nickname) {
        if(memberRepository.existsByNickname(nickname)) {
            throw new MemberException(ErrorCode.NICKNAME_DUPLICATE);
        }
        return false;
    }

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
}
```
<br>

## III. 회원가입 Controller 구현
### MemberController
회원가입과 관련된 API 엔드포인트를 제공합니다.<br>
이메일과 닉네임의 중복 체크, 회원가입, 회원가입 코드 이메일 발송 및 확인 기능을 포함합니다.

```java
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody SignUpReqDTO reqDTO) {
        Member member = mapper.singUpReqDTOToMember(reqDTO);
        member = memberService.addMember(member);
        SignUpResDTO memberRes = mapper.memberToSignUpResDTO(member);
        return new ResponseEntity<>(memberRes, HttpStatus.CREATED);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> emailCheck(@PathVariable(value = "email") String email) {
        return new ResponseEntity<>(memberService.existEmail(email), HttpStatus.OK);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Object> nicknameCheck(@PathVariable String nickname) {
        return new ResponseEntity<>(memberService.existNickname(nickname), HttpStatus.OK);
    }

    @PostMapping("/sendJoinCode")
    public ResponseEntity<Object> sendJoinCode(@RequestParam("email") String email) {
        MailDTO dto = memberService.createJoinCodeMail(email);
        memberService.mailSend(dto);
        return new ResponseEntity<>("메일 전송 완료", HttpStatus.OK);
    }

    // 회원가입 코드 일치 여부 체크
    @GetMapping("/joinCode/{joinCode}")
    public ResponseEntity<Object> joinCodeCheck(@PathVariable String joinCode) {
        return new ResponseEntity(memberService.checkJoinCode(joinCode), HttpStatus.OK);
    }
}

```
<br>

## IIII. 예외 처리
### MemberException
회원 관련 예외를 처리하기 위한 커스텀 예외 클래스입니다.

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
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    PW_NOT_FOUND(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
```
