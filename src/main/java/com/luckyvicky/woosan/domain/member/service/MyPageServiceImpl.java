package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import com.luckyvicky.woosan.domain.board.dto.WriterDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.jpa.ReplyRepository;
import com.luckyvicky.woosan.domain.likes.entity.Likes;
import com.luckyvicky.woosan.domain.likes.repository.LikesRepository;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import com.luckyvicky.woosan.domain.member.dto.MatchingMyPageDTO;
import com.luckyvicky.woosan.domain.messages.dto.MessageAddDTO;
import com.luckyvicky.woosan.domain.messages.entity.Message;
import com.luckyvicky.woosan.domain.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MyPageServiceImpl implements MyPageService{


    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MemberMatchingRepository memberMatchingRepository;



    //내가 작성한 게시글 조회(마이페이지)
    @Override
    public List<BoardDTO> getBoardsByWriterId(Long writerId) {
        List<Board> boards = boardRepository.findByWriterId(writerId);

        if (boards.isEmpty()) {
            throw new IllegalArgumentException("Member not found");
        }

        WriterDTO writerDTO = new WriterDTO();
        return boards.stream().map(board -> BoardDTO.builder()
                .id(board.getId())
                .writerId(writerDTO.builder()
                        .id(board.getWriter().getId())
                        .nickname(board.getWriter().getNickname())
                        .build().getId())
                .title(board.getTitle())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .views(board.getViews())
                .likesCount(board.getLikesCount())
                .categoryName(board.getCategoryName())
                .build()).collect(Collectors.toList());
    }


    //추천 게시물 조회(마이페이지)
    @Override
    public List<BoardDTO> getTargetIdByLikes(Long targetId) {
        List<Likes> likesList = likesRepository.findByTargetIdAndType(targetId, "게시물");
        List<Long> writerIds = likesList.stream()
                .map(likes -> likes.getMember().getId())
                .collect(Collectors.toList());
        List<Board> boards = boardRepository.findByWriterIdIn(writerIds);
        return boards.stream()
                .map(board -> new BoardDTO(
                        board.getId(),
                        board.getWriter().getId(),
                        board.getWriter().getNickname(),
                        null,
                        board.getTitle(),
                        board.getContent(),
                        board.getRegDate(),
                        board.getUpdateTime(),
                        board.getViews(),
                        board.getLikesCount(),
                        board.getCategoryName(),
                        board.getReplyCount(),
                        null,
                        null
                ))
                .collect(Collectors.toList());
    }

    //작성한 댓글 조회(마이페이지)
    @Override
    public List<ReplyDTO> getReplyByWriterId(Long writerId) {
        List<Reply> replies = replyRepository.findByWriterId(writerId);

        if (replies.isEmpty()) {
            throw new IllegalArgumentException("reply not found");
        }

        return replies.stream().map(reply -> ReplyDTO.builder()
                .id(reply.getId())
                .writerId(reply.getWriter().getId())
                .parentId(reply.getParentId())
                .boardId(reply.getBoard().getId())
                .content(reply.getContent())
                .regDate(reply.getRegDate())
                .likesCount(reply.getLikesCount())
                .build()).collect(Collectors.toList());
    }

    //보낸 쪽지함
    @Override
    public List<MessageAddDTO> getSendMessageById(Long senderId) {
        List<Message> messages = messageRepository.findBySenderId(senderId);

        if (messages.isEmpty()) {
            throw new IllegalArgumentException("reply not found");
        }

        return messages.stream()
                .filter(message -> message.getSender().getId().equals(senderId))
                .map(message -> MessageAddDTO.builder()
                        .senderId(message.getSender().getId())
                        .receiver(String.valueOf(message.getReceiver().getId())) // Assuming there is a getUsername() method in Member class
                        .content(message.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    //보낸 쪽지함
    @Override
    public List<MessageAddDTO> getReceiveMessageById(Long senderId) {
        List<Message> messages = messageRepository.findBySenderId(senderId);

        if (messages.isEmpty()) {
            throw new IllegalArgumentException("reply not found");
        }

        return messages.stream()
                .filter(message -> message.getSender().getId().equals(senderId))
                .map(message -> MessageAddDTO.builder()
                        .senderId(message.getSender().getId())
                        .receiver(String.valueOf(message.getReceiver().getId()))
                        .content(message.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    //매칭 조회
    @Override
    public List<MatchingMyPageDTO> getMatchingById(Long memberId) {
        // member_matching에서 사람 찾기
        List<MemberMatching> memberMatchings = memberMatchingRepository.findByMemberId(memberId);

        if (memberMatchings.isEmpty()) {
            throw new IllegalArgumentException("Matching not found for member id: " + memberId);
        }

        // 찾은 사람의 matching_board 정보 가져오기
        List<MatchingMyPageDTO> matchingMyPageDTOs = memberMatchings.stream()
                .map(memberMatching -> {
                    MatchingBoard matchingBoard = memberMatching.getMatchingBoard();
                    return MatchingMyPageDTO.builder()
                            .memberId(memberMatching.getMember().getId())
                            .matchingType(matchingBoard.getMatchingType())
                            .title(matchingBoard.getTitle())
                            .placeName(matchingBoard.getPlaceName())
                            .meetDate(matchingBoard.getMeetDate())
                            .headCount(matchingBoard.getHeadCount())
                            .build();
                })
                .collect(Collectors.toList());

        return matchingMyPageDTOs;
    }




}
