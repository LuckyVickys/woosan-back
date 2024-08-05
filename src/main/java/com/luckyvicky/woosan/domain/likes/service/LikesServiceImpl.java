package com.luckyvicky.woosan.domain.likes.service;

import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.jpa.ReplyRepository;
import com.luckyvicky.woosan.domain.likes.entity.Likes;
import com.luckyvicky.woosan.domain.likes.exception.LikeException;
import com.luckyvicky.woosan.domain.likes.repository.LikesRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.global.annotation.SlaveDBRequest;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.luckyvicky.woosan.global.util.Constants.*;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class LikesServiceImpl implements LikesService {


    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final ValidationHelper validationHelper;


    /**
     * 추천 토글
     */
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

    /**
     * 이미 추천이 되어 있는 경우, 추천 취소
     */
    public void handleLikeRemoval(Likes existingLike, String type, Long targetId,  Long memberId) {
        likesRepository.delete(existingLike);
        updateLikeCount(type, targetId, -1);
        updateMemberPoints(memberId, -5);
    }

    /**
     * 추천이 되어있지 않은 경우, 추천 추가
     */
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

    private void updateLikeCount(String type, Long targetId, int likesCount) {
        if (TYPE_BOARD.equals(type)) {
            Board board = validationHelper.findBoard(targetId);
            board.changeLikesCount(likesCount);
            boardRepository.save(board);
        } else if (TYPE_REPLY.equals(type)) {
            Reply reply = validationHelper.findReply(targetId);
            reply.changeLikesCount(likesCount);
            replyRepository.save(reply);
        } else {
            throw new LikeException(ErrorCode.INVALID_TYPE);
        }
    }

    private void updateMemberPoints(Long memberId, int points) {
        Member member = validationHelper.findWriterAndAddPoints(memberId, points);
        memberRepository.save(member);
    }

    /**
     * 추천 여부 확인
     */
    @SlaveDBRequest
    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long memberId, String type, Long targetId) {
        validationHelper.validateLikeInput(memberId, type, targetId);
        return likesRepository.existsByMemberIdAndTypeAndTargetId(memberId, type, targetId);
    }





}