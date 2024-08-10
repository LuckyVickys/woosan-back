# Login 로그인
## I. DTO 구현
### 1. LoginRequestDTO
사용자에게 이메일, 패스워드를 입력받기 위한 DTO입니다.

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "이메일 입력은 필수입니다.")
    @Email
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;
}
```

### 2. LoginResponseDTO
로그인 결과를 반환하는 DTO입니다.

```java
@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String email;
    private String nickname;
    private int point;
    private int nextPoint;
    private String memberType;
    private String level;
    private String accessToken;
    private String refreshToken;
    private Boolean isActive;
}
```

## II. 로그인 Service 구현
```LoginRequestDTO```에서 받은 이메일을 사용해 DB에서 해당 이메일을 가진 사용자를 조회합니다. <br>
만약 사용자가 존재하지 않으면 ```MEMBER_NOT_EXCEPTION``` 예외를 발생시킵니다. <br>
비밀번호가 일치하는지 확인하고, 일치하지 않는 경우 ```PW_NOT_FOUND``` 예외를 발생시킵니다.

### 핵심 기능
- **JWT 토큰 생성**: 사용자가 유효하면, 해당 사용자의 정보를 ```CustomUserInfoDTO```로 변환 후 ```JWTUtil``` 클래스를 사용하여 AccessToken과 RefreshToken을 생성합니다.

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

## III. 로그인 Controller 구현
### AuthController
로그인 API 엔드포인트를 제공합니다.

```java
@PostMapping("/login")
public ResponseEntity<LoginResponseDTO> getMemberInfo(
        @Valid @RequestBody LoginRequestDTO request,
        HttpServletResponse response
) {
    LoginResponseDTO dto = this.authService.login(request);
    String accessToken = dto.getAccessToken();
    response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
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

### ErrorCode
예외 처리에 사용할 오류 코드를 정의한 enum 클래스입니다.

```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "중복된 이메일입니다.");

    private final HttpStatus status;
    private final String message;
}
```
<br>

---

# AccessToken 만료 시 RefreshToken 사용해 AccessToken 재발급

## I. DTO 구현
### 1. RefreshTokenReqDTO
리프레시 토큰으로 액세스 토큰 발급 시 필요한 정보를 담은 DTO입니다.

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenReqDTO {
    private String refreshToken;
}
```

### 2. RefreshTokenResDTO
리프레시 토큰으로 액세스 토큰 발급 결과를 담은 DTO입니다.

```java
@Getter
@AllArgsConstructor
public class RefreshTokenResDTO {
    private String accessToken;
    private String refreshToken;
}

```

## II. Service 구현
### AuthServiceImpl
토큰 재발급 서비스 클래스입니다. <br>
request 객체에서 RefresToken을 추출하고, <br>
authHeader에서 "Bearer " 문자열을 제거하고 AccessToken을 추출합니다. <br>
추출한 AccessToken이 유효하다면 새로운 토큰을 발급할 필요가 없기 때문에 <br>
현재의 AccessToken과 RefreshToken을 그대로 클라이언트에게 반환합니다.
<br> <br>
AccessToken이 유효하지 않은 경우, RefreshToken의 유효성을 검증하여 <br>
유효하다면, RefreshToken으로부터 새로운 AccessToken을 생성합니다. <br>
새로 생성된 AccessToken과 기존 RefreshToken을 포함하는 ```RefreshTokenResDTO``` 객체를 만들어서 클라이언트에게 반환합니다.

```java
@Override
public ResponseEntity<?> refreshAccessToken(String authHeader, RefreshTokenReqDTO request) {
    String refreshToken = request.getRefreshToken();
    String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : "";

    if (jwtUtil.validateToken(accessToken)) {
        return ResponseEntity.ok(new RefreshTokenResDTO(accessToken, refreshToken));
    }

    if (jwtUtil.validateRefreshToken(refreshToken)) {
        try {
            String newAccessToken = jwtUtil.getAccessTokenFromRefreshToken(refreshToken);
            return ResponseEntity.ok(new RefreshTokenResDTO(newAccessToken, refreshToken));
        } catch (JWTException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
}
```

## III. Controller 구현
### AuthController
토큰 재발급과 관련된 API 엔드포인트를 제공합니다.

```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String authHeader, @RequestBody RefreshTokenReqDTO request) {
    return authService.refreshAccessToken(authHeader, request);
}
```

## IV. 예외 처리
### JWTException
JWT 관련 예외를 처리하기 위한 커스텀 예외 클래스입니다.

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
    // jwt 관련 오류
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Access Token이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh Token이 존재하지 않습니다."),
    INVALID_STRING(HttpStatus.BAD_REQUEST, "Invalid String");

    private final HttpStatus status;
    private final String message;
}
```
