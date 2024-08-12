# SocialLogin 소셜로그인
## I. DTO 구현
### CustomUserInfoDTO
사용자 정보를 담고 있는 DTO입니다. <br>
서비스 계층에서 사용자 정보를 전달하는 데 사용됩니다.

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomUserInfoDTO {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private Boolean isActive;
    private int point;
    private int nextPoint;
    private String level;
    private String memberType;
    private String socialType;

    public <T> CustomUserInfoDTO(T id, T email, T memberType, T level) {
    }
}
```

## II. 소셜 로그인 Service 구현
### AuthServiceImpl
카카오 소셜 로그인 요청을 처리하고, 사용자 정보를 관리하는 로직을 담당하는 클래스입니다. <br>
사용자의 accessToken을 인자로 받아 사용자 정보를 조회하거나 생성한 후, <br>
카카오 API로부터 사용자의 이메일을 조회합니다. <br>
DB에 해당 이메일로 등록된 사용자가 존재한다면, 사용자의 정보를 ```CustomUserInfoDTO```로 변환하여 반환합니다. <br>
존재하지 않는다면 생성된 사용자의 정보를 CustomUserInfoDTO로 변환하여 반환합니다.

```java
@Override
public LoginResponseDTO login(LoginRequestDTO dto) {
    String email = dto.getEmail();
    String password = dto.getPassword();
    Member member = memberRepository.findByEmail(email);

    if(member == null) {
        throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
    }

    if(!bCryptPasswordEncoder.matches(password, member.getPassword())) {
        throw new MemberException(ErrorCode.PW_NOT_FOUND);
    }

    CustomUserInfoDTO info = mapper.memberToCustomUserInfoDTO(member);
    String accessToken = jwtUtil.createAccessToken(info);
    String refreshToken = jwtUtil.createRefreshToken(info);

    return new LoginResponseDTO(member.getId(), member.getEmail(),
            member.getNickname(), member.getPoint(), member.getNextPoint(),
            member.getMemberType().toString(), member.getLevel().toString(),
            accessToken, refreshToken, member.getIsActive());
}
```

## III. 소셜 로그인 Controller 구현
### SocialController
카카오 로그인 API 엔드포인트를 제공합니다. <br>
```/api/oauth/kakao``` 엔드포인트가 호출되면 클라이언트로부터 accessToken을 전달받고, <br>
```AuthServiceImpl```의 ```getMember(accessToken)``` 메서드를 호출하여 카카오 사용자의 정보를 가져옵니다. <br>
사용자 정보를 기반으로 JWT AccessToken과 RefreshToken을 생성합니다. <br>
마지막으로, 사용자가 해당 정보를 이용해 이후 API 호출 시 인증할 수 있도록 <br>
사용자 정보와 토큰을 Map에 담아 반환합니다.

```java
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class SocialController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;

    @GetMapping("/kakao")
    public Map<String, Object> getMemberFromKakao(String accessToken) {

        log.info("accessToken ");
        log.info(accessToken);

        CustomUserInfoDTO user = authService.getKakaoMember(accessToken);

        String jwtAccessToken = jwtUtil.createAccessToken(user);
        String jwtRefreshToken = jwtUtil.createRefreshToken(user);

        authService.getKakaoMember(accessToken);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("nickname", user.getNickname());
        claims.put("isActive", user.getIsActive());
        claims.put("memberType", user.getMemberType());
        claims.put("socialType", user.getSocialType());
        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }
}
```

## IV. 예외 처리
### MemberException
로그인 예외를 처리하기 위한 커스텀 예외 클래스입니다.

```java
public class MemberException extends GlobalException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

### JWTException
JWT 토큰을 예외를 처리하기 위한 커스텀 예외 클래스입니다.

```java
public class JWTException extends GlobalException {
    public JWTException(ErrorCode errorCode) {
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
    // 멤버 관련 오류
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    PW_NOT_FOUND(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // jwt 관련 오류
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Access Token이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
```
