# FileImg

## Entity
- MemberProfile, Board, MatchingBoard, Admin Banner 등 파일 업로드 관련 기능을 사용할때 type, targetId를 활용하여 type에 대상 유형 (MemberProfile, Board, MatchingBoard, Admin), targetId에 대상의 고유번호 id를 저장하여 하나의 테이블에서 사진 파일을 관리 할 수 있도록 Entity를 설계했습니다.
- Object Storage에서 파일이름이 중복되어도 문제가 발생하지 않도록 uuid + _ + fileName 형태를 활용하여 저장하도록 설계했습니다.

```java
package com.luckyvicky.woosan.domain.fileImg.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, length = 255)
    private String type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "uuid", nullable = false, length = 255)
    private String uuid;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "ord")
    private Integer ord;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

}
```

## FileImg 함수
회원 프로필, 게시판 사진 등록 등 다양한 모듈에서 파일 업로드, 조회, 삭제 기능을 손쉽게 구현할 수 있도록 재사용 가능한 함수들을 제공합니다. 
이러한 함수들은 파일 관리의 복잡성을 줄이고, 일관된 방식으로 파일 작업을 처리할 수 있도록 설계되었습니다.

### 파일 업로드
```java
    @Override
    public void fileUploadMultiple(String type, Long targetId, List<MultipartFile> files) {
        FileImgUtils.saveFiles(s3, fileImgRepository, bucketName, type, targetId, files);
    }
```
  - 활용 예시 (게시판 사진 업로드)
    ```java
      @Override
      public void createBoard(BoardDTO boardDTO, List<MultipartFile> images) {
          validationHelper.boardInput(boardDTO); // 입력값 검증
          Member writer = validationHelper.findWriterAndAddPoints(boardDTO.getWriterId(), 10); // 작성자 검증 및 조회
          memberRepository.save(writer);
          Board board = saveBoard(boardDTO, writer);
          handleFileUpload(images, board.getId()); //파일이 있으면 파일 정보를 버킷 및 db에 저장
      }
  
      private void handleFileUpload(List<MultipartFile> images, Long boardId) {
          if(images != null){
              fileImgService.fileUploadMultiple("board", boardId, images);
          }
      }
    ```
    
### 파일 조회
```java
    @SlaveDBRequest
    @Override
    public List<String> findFiles(String type, Long targetId) {
        return fileImgRepository.findByTypeAndTargetIdOrderByOrdAsc(type, targetId).stream()
                .map(fileImg -> s3.getUrl(bucketName + fileImg.getPath(), fileImg.getUuid().toString() + "_" + fileImg.getFileName()).toString())
                .collect(Collectors.toList());
    }
```
  - 활용 예시 (게시판 사진, 작성자 프로필 조회)
    ```java
        @Override
        @Transactional
        public BoardDetailDTO getBoard(Long id) {
            increaseViewCount(id); // 조회수 증가
            Board board = validationHelper.findBoard(id); // 게시물 조회
            IBoardMember boardMember = boardRepository.findById(id, IBoardMember.class)
                    .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
            BoardDTO boardDTO = commonUtils.mapObject(boardMember, BoardDTO.class);
            boardDTO.setViews(board.getViews());  // 최신 조회수 DTO에 반영
            boardDTO.setFilePathUrl(fileImgService.findFiles("board", id));   // 버킷에서 이미지 url 꺼내고 DTO에 반영
            boardDTO.setWriterProfile(fileImgService.findFiles("member", boardDTO.getWriterId()));   // 버킷에서 이미지 url 꺼내고 DTO에 반영
    
    
            // 연관 게시물 검색
            List<SuggestedBoardDTO> suggestedBoards = elasticsearchBoardService.getSuggestedBoards(id, board.getTitle(), board.getContent());
    
            return BoardDetailDTO.builder()
                    .boardDTO(boardDTO)
                    .suggestedBoards(suggestedBoards)
                    .build();
        }
    ```

## 트러블 슈팅 Object Storage를 이용한 다중 파일 수정 및 삭제

### 파일 수정 프로세스

파일 수정 작업 시, 기존 파일과 새로 추가된 파일을 효율적으로 관리하기 위해 다음과 같은 프로세스를 따릅니다.

### 1. 수정 요청 처리
- 사용자가 수정 요청을 할 때, 기존 파일들의 URL 리스트 (`List<String>`)와 새로 추가된 파일 리스트 (`List<MultipartFile>`)를 함께 받습니다.
  
### 2. 기존 파일 삭제
- 기존 URL 리스트 중에서 수정 요청에서 전달된 새 URL 리스트에 포함되지 않은 파일을 찾아냅니다.
- 해당 파일들을 데이터베이스와 Object Storage에서 삭제하여 불필요한 파일이 남지 않도록 처리합니다.

### 3. 새로운 파일 추가
- 새로 추가된 `List<MultipartFile>`의 파일들을 데이터베이스와 Object Storage에 추가합니다.
- 이 과정에서 파일을 저장하고 관련 메타데이터를 데이터베이스에 저장하여, 이후 파일 조회 및 관리를 쉽게 할 수 있도록 합니다.

### DTO형태 
```java
    //기존 파일 Url 
    private List<MultipartFile> images;
    //새로 추가된 파일 
    private List<String> filePathUrl;
```

### 과제
게시판 수정 및 관리자 배너 작업 시, 데이터베이스와 Object Storage에 저장된 파일을 함께 삭제해야 하는 상황이 발생했습니다.

### 해결 방법

1. **URL 분석**:
    - Object Storage 엔드포인트와 버킷 이름 뒤에 파일 이름이 저장됨을 발견했습니다.
    - 이를 통해 파일 삭제 작업을 진행할 수 있었습니다.

2. **한글 파일 삭제 문제**:
    - 영문 파일의 경우, URL에서 파일 이름을 추출하여 삭제할 수 있었지만, 한글 파일은 삭제되지 않는 문제가 발생했습니다.

3. **해결책 적용**:
    - URL 인코딩을 통해 파일 이름을 인코딩하고, 이를 URL과 동일하게 매칭시켰습니다.
    - 데이터베이스의 ID 값을 Map 컬렉션의 key로, URL 인코딩 값을 value로 설정하여 매핑하였습니다.
    - 이렇게 매칭된 값을 기반으로 삭제된 파일들을 비교하여 파일이 정확하게 삭제되도록 구현했습니다.
### 관련 코드
1. 게시판 사진 파일 수정 요청
  - 게시판 이미지 파일이 수정될 때, 기존 파일의 존재 여부에 따라 파일을 삭제하거나 새 파일을 업로드하는 작업을 수행합니다.
     ```java
      private void updateBoardFiles(BoardDTO boardDTO, List<MultipartFile> images, Long boardId) {
          if (boardDTO.getFilePathUrl() == null) {
              fileImgService.targetFilesDelete("board", boardId);
          } else {
              List<String> beforeFiles = fileImgService.findFiles("board", boardId);
              List<String> afterFiles = boardDTO.getFilePathUrl();
  
              for (String beforeFile : beforeFiles) {
                  if (!afterFiles.contains(beforeFile)) {
                      fileImgService.deleteS3FileByUrl(boardId, "board", beforeFile);
                  }
              }
          }
  
          if (images != null) {
              fileImgService.fileUploadMultiple("board", boardId, images);
          }
      }
  - 기능 설명
    -   기존 파일 삭제: boardDTO.getFilePathUrl()가 null이면, 해당 게시물(boardId)의 모든 파일을 삭제합니다.
    -   파일 비교 후 삭제: 기존 파일 목록과 새로운 파일 목록을 비교하여, 기존 파일 중에서 새로운 파일 목록에 없는 파일을 삭제합니다.
    -   새로운 파일 업로드: 업로드할 이미지가 존재하면, 이를 S3에 업로드하고 데이터베이스에 기록합니다.
  
2. Url을 활용한 파일 정보 확인 및 한글파일 인식
   - URL을 활용하여 파일 이름을 추출하고, 해당 파일이 S3와 데이터베이스에 존재하는지 확인 후 삭제하는 작업을 수행합니다. 이 과정에서 한글 파일명도 인식할 수 있도록 인코딩/디코딩 처리가 포함되어 있습니다.
    ```java
      @Override
      public void deleteS3FileByUrl(Long id, String type, String beforeFile) {
          //기존 파일 List
          List<FileImg> existFiles = fileImgRepository.findByTypeAndTargetIdOrderByOrdAsc(type, id);
          Map<Long, String> targetMap = new HashMap<>();
          //Url에서 파일 이름만 잘라서 변수에 저장
          String getFileName = FileImgUtils.getFileNameInUrl(beforeFile);
          FileImgUtils.populateTargetMap(existFiles, targetMap);
  
          FileImgUtils.deleteFileFromTargetMap(s3, fileImgRepository, bucketName, targetMap, getFileName, type);
      }
    ```
  - 기능 설명
    - 파일 이름 추출: URL에서 파일 이름을 추출하여 비교 작업에 사용합니다.
    - 타겟 맵 생성: 데이터베이스에서 조회한 파일 목록을 기반으로 타겟 맵을 생성합니다. 한글 파일명도 처리할 수 있도록 파일명을 인코딩합니다.
    - 파일 삭제: 타겟 맵에 없는 파일을 삭제합니다.
    
3. URL 인코딩을 통한 파일 인식
  - 파일명이 한글이거나 특수문자가 포함된 경우에도 정확히 인식할 수 있도록 URL 인코딩 및 디코딩을 사용합니다.

    타겟 맵 생성
    ```java
      public static void populateTargetMap(List<FileImg> existFiles, Map<Long, String> targetMap) {
          for (FileImg fileImg : existFiles) {
              try {
                  String encodedFileName = URLEncoder.encode(fileImg.getFileName(), StandardCharsets.UTF_8.toString()).replace("+", "%20");
                  targetMap.put(fileImg.getId(), fileImg.getUuid() + "_" + encodedFileName);
              } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              }
          }
      }
    ```

    파일 삭제 로직
    ```java
    public static void deleteFileFromTargetMap(AmazonS3 s3, FileImgRepository fileImgRepository, String bucketName, Map<Long, String> targetMap, String getFileName, String type) {
        for (Map.Entry<Long, String> entry : targetMap.entrySet()) {
            try {
                String decodedEntryValue = URLDecoder.decode(entry.getValue(), StandardCharsets.UTF_8.toString());
                if (decodedEntryValue.equals(getFileName)) {
                    FileImg targetImg = fileImgRepository.findById(entry.getKey()).orElse(null);
                    if (targetImg != null) {
                        fileImgRepository.deleteById(entry.getKey());
                        s3.deleteObject(bucketName, type + "/" + targetImg.getUuid() + "_" + targetImg.getFileName());
                    }
                    break;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
    ```
  - 기능 설명
    - 한글 파일명 처리: URLEncoder와 URLDecoder를 사용하여 한글 파일명을 인코딩/디코딩합니다. 이는 한글 파일명이 URL 인코딩된 상태로 처리되기 때문입니다.
    - 타겟 파일 삭제: 인코딩된 파일 이름과 데이터베이스에 저장된 파일명을 비교하여, 일치하는 파일을 S3와 데이터베이스에서 삭제합니다.

### 결과
이와 같은 접근 방식을 통해, Object Storage에 저장된 다중 파일을 데이터베이스와 함께 일관되게 관리하고, 삭제 시 발생할 수 있는 문제를 해결할 수 있었습니다.


