# I. DTO 구현
## MemberInfoDTO
로그인 후 헤더에 있는 프로필 사진을 클릭하면 보여줄 회원 정보를 포함한 DTO 클래스입니다.

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MemberInfoDTO {
    private Long id;
    private String email;
    private String nickname;
    private int point;
    private int nextPoint;
    private String memberType;
    private String level;
    private List<String> profile;
}
```

# II. 회원 정보 조회 Service 구현
## MemberServiceImpl
회원 정보 조회 서비스 클래스입니다. <br>
로그인 한 유저의 이메일로 회원 정보를 불러옵니다.

```java
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberServiceImpl implements MemberService {
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
```

# III. 회원 정보 Controller 구현
## MemberController
회원 정보와 관련된 API 엔드포인트를 제공합니다.

```java
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    @GetMapping("/info")
    public ResponseEntity<Object> memberInfo(@RequestParam("email") String email) {
        return new ResponseEntity(memberService.getMemberInfo(email), HttpStatus.OK);
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
