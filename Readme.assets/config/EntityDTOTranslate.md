# Entity와 DTO 변환 설정
## I. ModelMapper

### 1. build.gradle 설정
modelMapper 의존성을 추가합니다.

```java
dependencies {
  implementation 'org.modelmapper:modelmapper:3.1.0'
}
```

### 2. ModelMapper 등록 및 설정 클래스
ModelMapper를 빈으로 등록하고, <br>
ModelMapper가 필드명을 기준으로 매칭을 시도할 수 있고 유연한 매핑이 가능하도록 설정합니다.

```java
@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        return modelMapper;
    }
}
```

## II. MapStruct

### 1. build.gradle 설정
```java
dependencies {
  implementation 'org.mapstruct:mapstruct:1.5.1.Final'
	annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.1.Final'
}
```

### 2. Mapper 인터페이스

- MemberMapper
Member 엔티티와 회원 DTO 간 변환을 담당하는 매퍼 인터페이스입니다.

```java
@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    Member singUpReqDTOToMember(SignUpReqDTO signUpReqDTO);
    SignUpResDTO memberToSignUpResDTO(Member member);
    CustomUserInfoDTO memberToCustomUserInfoDTO(Member member);
    MemberInfoDTO memberToMemberInfoDTO(Member member);
}
```

- MessageMapper
Message 엔티티와 MessageDTO 간 변환을 담당하는 매퍼 인터페이스입니다.

```java
@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    MessageDTO messageToMessageDTO(Message message);
}
```

- ReportMapper
Report 엔티티와 ReportDTO 간 변환을 담당하는 매퍼 인터페이스입니다.

```java
@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportMapper {
    ReportDTO reportToReportDTO(Report report);
}
```

- MatchingBoardMapper
Matching 엔티티와 만남 DTO 간 변환을 담당하는 매퍼 인터페이스입니다.

```java
@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchingBoardMapper {

    @Mapping(source = "member.id", target = "memberId") // member의 id를 memberId로 매핑
    MatchingBoardResponseDTO toResponseDTO(MatchingBoard matchingBoard);

    @Mapping(source = "memberId", target = "member.id") // memberId를 member의 id로 매핑
    MatchingBoard toEntity(MatchingBoardRequestDTO requestDTO);
}
```

- MemberMatchingMapper
Member, Matching 테이블의 다대다 중간 테이블의 역할을 하는 MemberMatching 엔티티와 DTO 간 변환을 담당하는 매퍼 인터페이스입니다.

```java
@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMatchingMapper {
    MemberMatchingMapper INSTANCE = Mappers.getMapper(MemberMatchingMapper.class);

    // 요청 DTO를 엔티티로 변환
    @Mapping(target = "matchingBoard.id", source = "matchingId")
    @Mapping(target = "member.id", source = "memberId")
    MemberMatching toEntity(MemberMatchingRequestDTO requestDTO);

    // 엔티티를 응답 DTO로 변환
    @Mapping(target = "matchingId", source = "matchingBoard.id")
    @Mapping(target = "memberId", source = "member.id")
    MemberMatchingResponseDTO toDto(MemberMatching memberMatching);
}
```

- MatchingBoardReplyMapper
MatchingBoardReply 엔티티와 만남 댓글 DTO 간 변환을 담당하는 매퍼 인터페이스입니다.

```java
@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchingBoardReplyMapper {

    @Mapping(source = "writer.id", target = "writerId")
    MatchingBoardReplyResponseDTO toResponseDTO(MatchingBoardReply matchingBoardReply);

    @Mapping(source = "writerId", target = "writer.id")
    MatchingBoardReply toEntity(MatchingBoardReplyRequestDTO requestDTO);
}
```
