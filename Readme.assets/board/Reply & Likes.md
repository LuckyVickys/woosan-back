# Reply & Likes 기능

## Reply 기능

### Overview
사용자가 게시물에 댓글을 달고, 조회하며, 관리합니다. 댓글은 게시물의 상호작용을 촉진하며, 사용자 간의 소통을 강화합니다. 또한, 댓글은 부모-자식 구조를 통해 대댓글 기능을 제공하며, 댓글 삭제 시에는 자식 댓글도 함께 삭제됩니다.

### Structure
- **ReplyController**: 댓글 작성, 조회, 삭제와 관련된 API 엔드포인트를 제공합니다.
- **ReplyService**: 댓글 작성, 조회, 삭제와 관련된 비즈니스 로직을 처리합니다.
- **ReplyRepository**: JPA를 사용하여 댓글 데이터를 데이터베이스에 저장하고 조회하는 역할을 합니다.
- **Reply**: 댓글의 엔티티 클래스로, 댓글의 모든 속성을 정의하고 관리합니다.

### Features

1. **댓글 작성**
   - **기능 설명**: 사용자가 게시물에 댓글을 작성할 수 있는 기능입니다. 댓글 작성 시, 작성자는 자동으로 일정 포인트를 부여받으며, 해당 게시물의 댓글 수가 증가합니다.
     - 사용자가 입력한 댓글 내용을 바탕으로 새로운 댓글을 생성하고, 데이터베이스에 저장합니다.
     - 댓글 작성 시 부모 댓글이 있는 경우, 해당 댓글의 부모 ID를 통해 대댓글을 작성할 수 있습니다.
     
     ```java
     @Override
     @Transactional
     public void createReply(ReplyDTO replyDTO) {
         validationHelper.replyInput(replyDTO); // 필수 입력값 검증

         // 게시글과 작성자 조회
         Board board = validationHelper.findBoard(replyDTO.getBoardId());
         Member writer = validationHelper.findWriterAndAddPoints(replyDTO.getWriterId(), 1);

         Reply reply = buildReply(replyDTO, board, writer);

         memberRepository.save(writer);
         board.changeReplyCount(+1);
         boardRepository.save(board);
         replyRepository.save(reply);
     }
     ```

2. **댓글 조회**
   - **기능 설명**: 특정 게시물에 달린 모든 댓글을 조회하는 기능입니다. 댓글은 계층 구조로 조회되며, 부모 댓글과 대댓글이 함께 제공됩니다.
     - 특정 게시물 ID를 기준으로 해당 게시물에 달린 모든 댓글을 조회하고, 댓글 계층 구조를 유지합니다.
     - 각 댓글은 작성자의 프로필 이미지 URL을 포함하여 반환됩니다.
     
     ```java
     @SlaveDBRequest
     @Override
     @Transactional(readOnly = true)
     public PageResponseDTO<ReplyDTO> getReplies(Long boardId, PageRequestDTO pageRequestDTO) {
         validationHelper.boardExist(boardId);

         Pageable pageable = commonUtils.createPageable(pageRequestDTO);
         Page<IReply> parentReplies = replyRepository.findByBoardIdAndParentIdIsNull(boardId, pageable);

         List<ReplyDTO> ReplyDTOs = parentReplies.getContent().stream()
                 .map(this::convertToReplyDTOWithChildren)
                 .collect(Collectors.toList());

         return commonUtils.createPageResponseDTO(pageRequestDTO, ReplyDTOs, parentReplies.getTotalElements());
     }

     private ReplyDTO convertToReplyDTOWithChildren(IReply iReply) {
         ReplyDTO replyDTO = commonUtils.mapObject(iReply, ReplyDTO.class);
         replyDTO.setWriterProfile(fileImgService.findFiles("member", iReply.getWriter().getId()));

         List<ReplyDTO> childReplyDTOs = replyRepository.findByParentId(iReply.getId()).stream()
                 .map(childReply -> {
                     ReplyDTO childReplyDTO = commonUtils.mapObject(childReply, ReplyDTO.class);
                     childReplyDTO.setWriterProfile(fileImgService.findFiles("member", childReply.getWriter().getId()));
                     return childReplyDTO;
                 })
                 .collect(Collectors.toList());

         replyDTO.setChildren(childReplyDTOs);
         return replyDTO;
     }
     ```

3. **댓글 삭제**
   - **기능 설명**: 사용자가 작성한 댓글을 삭제하는 기능입니다. 댓글을 삭제하면 해당 댓글에 달린 모든 대댓글도 함께 삭제됩니다.
     - 특정 댓글을 삭제하며, 해당 댓글의 자식 댓글이 있는 경우, 자식 댓글도 함께 삭제됩니다.
     - 댓글 삭제 시, 게시물의 댓글 수가 업데이트됩니다.
     
     ```java
     @Override
     @Transactional
     public void deleteReply(RemoveDTO removeDTO) {
         validationHelper.replyExist(removeDTO.getId());

         Reply reply = validationHelper.findReply(removeDTO.getId()); // 부모 댓글 조회
         validationHelper.checkReplyOwnership(reply, removeDTO.getWriterId()); // 작성자 검증

         int childReplyCount = deleteChildReplies(reply.getId()); // 자식 댓글 수 계산 및 삭제

         Board board = reply.getBoard(); 
         board.changeReplyCount(-(childReplyCount + 1));  // 자식 댓글 수와 부모 댓글을 포함한 삭제
         boardRepository.save(board);

         replyRepository.delete(reply);
     }

     private int deleteChildReplies(Long parentId) {
         List<IReply> childReplies = replyRepository.findByParentId(parentId);
         int count = 0;

         for (IReply child : childReplies) {
             count += deleteChildReplies(child.getId()) + 1;
             replyRepository.deleteById(child.getId());
         }

         return count;
     }
     ```

---

## Likes 기능

### Overview
사용자가 게시물이나 댓글에 대해 추천(좋아요)을 할 수 있는 기능을 제공합니다. 
추천 상태를 토글(추가 및 취소)하고, 현재 추천 여부를 확인할 수 있습니다. 추천을 통해 사용자는 게시물이나 댓글에 긍정적인 피드백을 남길 수 있으며, 추천 수에 따라 게시물이나 댓글의 인기도를 반영할 수 있습니다.

### Structure

- **LikesController**: 추천 기능과 관련된 API 엔드포인트를 제공합니다. 추천 상태를 토글하거나 추천 여부를 확인할 수 있습니다.
- **LikesService**: 추천의 추가 및 취소, 추천 여부 확인 등 추천 기능과 관련된 비즈니스 로직을 처리합니다.
- **LikesRepository**: JPA를 사용하여 추천 데이터를 데이터베이스에 저장하고 조회하는 역할을 합니다.
- **Likes**: 추천 엔티티 클래스로, 추천의 모든 속성을 정의하고 관리합니다.

### Features

1. **추천 토글**
   - **기능 설명**: 사용자가 게시물이나 댓글에 대해 추천을 추가하거나 취소할 수 있는 기능입니다. 
     - 사용자가 이미 추천한 경우, 해당 추천을 취소하고 추천 수를 감소시킵니다.
     - 사용자가 아직 추천하지 않은 경우, 추천을 추가하고 추천 수를 증가시킵니다.
     
     ```java
     @Override
     @Transactional
     public void toggleLike(Long memberId, String type, Long targetId) {
         validationHelper.validateLikeInput(memberId, type, targetId);
         Optional<Likes> existingLike = likesRepository.findByMemberIdAndTypeAndTargetId(memberId, type, targetId);

         if (existingLike.isPresent()) {
             handleLikeRemoval(existingLike.get(), type, targetId, memberId);
         } else {
             handleNewLike(type, targetId, memberId);
         }
     }

     public void handleLikeRemoval(Likes existingLike, String type, Long targetId, Long memberId) {
         likesRepository.delete(existingLike);
         updateLikeCount(type, targetId, -1);
         updateMemberPoints(memberId, -5);
     }

     private void handleNewLike(String type, Long targetId, Long memberId) {
         Member member = validationHelper.findWriterAndAddPoints(memberId, 5);
         Likes newLike = Likes.builder()
                 .member(member)
                 .type(type)
                 .targetId(targetId)
                 .build();
         likesRepository.save(newLike);
         updateLikeCount(type, targetId, 1);
         memberRepository.save(member);
     }
     ```

2. **추천 여부 확인**
   - **기능 설명**: 사용자가 특정 게시물이나 댓글에 대해 추천을 했는지 여부를 확인하는 기능입니다.
     - 사용자의 ID, 추천 대상의 타입(게시물 또는 댓글), 그리고 추천 대상의 ID를 입력받아, 이미 추천했는지 여부를 반환합니다.
     
     ```java
     @SlaveDBRequest
     @Override
     @Transactional(readOnly = true)
     public boolean isLiked(Long memberId, String type, Long targetId) {
         validationHelper.validateLikeInput(memberId, type, targetId);
         return likesRepository.existsByMemberIdAndTypeAndTargetId(memberId, type, targetId);
     }
     ```
