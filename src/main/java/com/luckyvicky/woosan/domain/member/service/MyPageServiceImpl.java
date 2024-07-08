package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import com.luckyvicky.woosan.domain.board.dto.WriterDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.ReplyRepository;
import com.luckyvicky.woosan.domain.likes.dto.ToggleRequestDTO;
import com.luckyvicky.woosan.domain.likes.entity.Likes;
import com.luckyvicky.woosan.domain.likes.repository.LikesRepository;
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
                        null, // Assuming writerProfile is not available directly
                        board.getTitle(),
                        board.getContent(),
                        board.getRegDate(),
                        board.getViews(),
                        board.getLikesCount(),
                        board.getCategoryName(),
                        board.getReplyCount(),
                        null, // Assuming images are not available directly
                        null  // Assuming filePathUrl is not available directly
                ))
                .collect(Collectors.toList());
    }

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



}
