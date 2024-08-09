# 쪽지 전송 기능
## I. Entity, DTO 구현
1. Message Entity
쪽지 테이블과 매핑되는 Message 엔티티 클래스입니다. <br>
쪽지 전송 시 필요한 정보를 포함합니다.

```java
package com.luckyvicky.woosan.domain.messages.entity;

import com.luckyvicky.woosan.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime regDate;

    @ColumnDefault("false")
    private Boolean delBySender;

    @ColumnDefault("false")
    private Boolean delByReceiver;

    public void changeIsDelBySender() {
        this.delBySender = true;
    }

    public void changeIsDelByReceiver() {
        this.delByReceiver = true;
    }
}

```

2. MessageAddDTO
쪽지 전송 시 필요한 정보를 담은 DTO 클래스입니다.

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageAddDTO {

    private Long senderId;
    private String receiver;

    @NotBlank(message = "쪽지 내용은 필수 항목입니다.")
    @Size(min = 1, max = 100, message = "쪽지 내용은 1자 이상 100자 이하여야 합니다.")
    private String content;
}
```

## II. 쪽지 전송 Service 구현
### MessageServiceImpl
쪽지 전송 서비스 클래스입니다. <br>
쪽지 송신자와 수신자, 쪽지의 유효성을 검사하고, <br>
해당 회원이 존재하지 않거나 쪽지가 비어있는 경우 예외를 던집니다.

```java
@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;

    // 쪽지 전송
    @Override
    public Long add(MessageAddDTO messageAddDTO) {
        Member sender = findMemberById(messageAddDTO.getSenderId());
        Member receiver = findMemberByNickname(messageAddDTO.getReceiver());
        validateMessageContent(messageAddDTO.getContent());

        Message message = createMessage(sender, receiver, messageAddDTO.getContent());
        Message savedMessage = messageRepository.save(message);

        return savedMessage.getId();
    }

    // id로 멤버를 찾기
    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 닉네임으로 멤버를 찾기
    private Member findMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 메시지 내용 검증
    private void validateMessageContent(String content) {
        if (content.trim().isEmpty()) {
            throw new GlobalException(ErrorCode.NULL_OR_BLANK);
        }
    }

    // 메시지 객체 생성
    private Message createMessage(Member sender, Member receiver, String content) {
        return Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .delBySender(false)
                .delByReceiver(false)
                .build();
    }
}
```

## III. 메시지 전송 Controller 구현
### MessageController
메시지 전송 API 엔드포인트를 제공합니다.

```java
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/add")
    public ResponseEntity<Long> register(@RequestBody MessageAddDTO messageAddDTO) {

        Long messageId = messageService.add(messageAddDTO);
        return ResponseEntity.ok(messageId);
    }
}
```

## IIII. 예외 처리
### GlobalException
전반적인 예외를 처리하기 위한 커스텀 예외 클래스입니다.

```java
@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    public GlobalException(ErrorCode errorCode) {
        this.errorCode = errorCode;
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

    // 공통 오류
    NULL_OR_BLANK(HttpStatus.BAD_REQUEST, "필수 입력값을 입력해주세요.");
```
