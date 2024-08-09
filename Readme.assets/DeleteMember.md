# 회원 탈퇴
## I. DTO 구현
### 1. DeleteRequestDTO
회원 탈퇴 시 필요한 회원 정보를 포함한 DTO 클래스입니다.

```java
@Data
@AllArgsConstructor
public class DeleteRequestDTO {
    @Email
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;
}
```

## II. 회원 탈퇴 Service 구현
### MemberServiceImpl
회원 탈퇴 서비스 클래스입니다.  <br>

```java
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberServiceImpl implements MemberService {
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
}
```

## III. 회원 탈퇴 Controller 구현
### MemberController
회원 탈퇴와 관련된 API 엔드포인트를 제공합니다.

```java
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    @PutMapping("/delete")
    public ResponseEntity<Object> deleteMember(@RequestBody DeleteRequestDTO deleteRequestDTO) {
        return new ResponseEntity<>(memberService.deleteMember(deleteRequestDTO), HttpStatus.OK);
    }
}
```
