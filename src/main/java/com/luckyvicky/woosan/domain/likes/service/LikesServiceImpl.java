package com.luckyvicky.woosan.domain.likes.service;

import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.exception.BoardException;
import com.luckyvicky.woosan.domain.board.exception.ReplyException;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.jpa.ReplyRepository;
import com.luckyvicky.woosan.domain.likes.entity.Likes;
import com.luckyvicky.woosan.domain.likes.exception.LikeException;
import com.luckyvicky.woosan.domain.likes.repository.LikesRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class LikesServiceImpl implements LikesService {


    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;


    /**
     * 추천 토글
     */
    @Override
    @Transactional
    public void toggleLike(Long memberId, String type, Long targetId) {
        //  입력값 검증
        validateInput(memberId, type, targetId);

        Optional<Likes> existingLike = likesRepository.findByMemberIdAndTypeAndTargetId(memberId, type, targetId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (existingLike.isPresent()) {
            // 이미 추천이 되어 있는 경우, 추천 취소
            likesRepository.delete(existingLike.get());

            // 추천수 감소
            updateLikeCount(type, targetId, -1);

            // 작성자 포인트 감소
            member.addPoint(-5);
        } else {
            // 추천이 되어있지 않은 경우, 추천 추가
            Likes newLike = Likes.builder()
                    .member(member)
                    .type(type)
                    .targetId(targetId)
                    .build();
            likesRepository.save(newLike);

            // 추천수 증가
            updateLikeCount(type, targetId, 1);

            // 작성자 포인트 추가
            member.addPoint(5);
        }

        memberRepository.save(member);
    }


    /**
     * 추천 여부 확인
     */
    private void updateLikeCount(String type, Long targetId, int likesCount) {

        if ("게시물".equals(type)) {
            Board board = boardRepository.findById(targetId)
                    .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
            board.changeLikesCount(likesCount);
            boardRepository.save(board);
        } else if ("댓글".equals(type)) {
            Reply reply = replyRepository.findById(targetId)
                    .orElseThrow(() -> new ReplyException(ErrorCode.REPLY_NOT_FOUND));
            reply.changeLikesCount(likesCount);
            replyRepository.save(reply);
        } else {
            throw new LikeException(ErrorCode.INVALID_TYPE);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long memberId, String type, Long targetId) {
        validateInput(memberId, type, targetId);
        return likesRepository.existsByMemberIdAndTypeAndTargetId(memberId, type, targetId);
    }


//    <--------------------------------예외처리-------------------------------->


    /**
     * 사용자, 타입, 타겟 검증
     */
    private void validateInput(Long memberId, String type, Long targetId) {
        if (type == null || type.isEmpty() || (!type.equals("댓글") && !type.equals("게시물"))) {
            throw new LikeException(ErrorCode.INVALID_TYPE);
        }
        if (memberId == null || memberId < 0) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (targetId == null || targetId < 0) {
            throw new LikeException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }


}