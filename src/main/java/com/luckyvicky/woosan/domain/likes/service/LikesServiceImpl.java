package com.luckyvicky.woosan.domain.likes.service;


import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.ReplyRepository;
import com.luckyvicky.woosan.domain.likes.entity.Likes;
import com.luckyvicky.woosan.domain.likes.repository.LikesRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
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

    @Override
    @Transactional
    public void toggleLike(Long memberId, String type, Long targetId) {
        Optional<Likes> existingLike = likesRepository.findByMemberIdAndTypeAndTargetId(memberId, type, targetId);
        Member member = memberRepository.findById(memberId).orElseThrow();

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

    private void updateLikeCount(String type, Long targetId, int likesCount) {
        if ("게시물".equals(type)) {
            Board board = boardRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물 ID"));
            board.changeLikesCount(likesCount);
            boardRepository.save(board);
        } else if ("댓글".equals(type)) {
            Reply reply = replyRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 ID"));
            reply.changeLikesCount(likesCount);
            replyRepository.save(reply);
        } else {
            throw new IllegalArgumentException("유효하지 않은 타입: " + type);
        }
    }

}

//    public void toggleLike(Long memberId, String type, Long targetId) {
//        Optional<Likes> existingLike = likesRepository.findByMemberIdAndTypeAndTargetId(memberId, type, targetId);
//        Member member = memberRepository.findById(memberId).orElseThrow();
//
//        if(existingLike.isPresent()){
//            // 이미 추천이 되어 있는 경우, 추천 취소
//            member.addPoint(-5);
//
//            likesRepository.delete(existingLike.get()); // Optional에서 Likes 객체를 꺼냄
//            decrementLikesCount(type, targetId);
//        } else {
//            // 추천이 되어있지 않은 경우, 추천 추가
//            member.addPoint(5);
//            memberRepository.save(member);
//
//            Likes newLike = Likes.builder()
//                    .member(member)
//                    .type(type)
//                    .targetId(targetId)
//                    .build();
//            likesRepository.save(newLike);
//            incrementLikeCount(type, targetId);
//        }

//        private void incrementLikeCount(String type, Long targetId) {
//        if (type.equals("게시물")) {
//            Board board = boardRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물 ID"));
//            board.incrementLikesCount();
//            boardRepository.save(board);
//        } else if (type.equals("댓글")) {
//            Reply reply = replyRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 ID"));
//            reply.incrementLikesCount();
//            replyRepository.save(reply);
//        }
//    }
//
//    private void decrementLikesCount(String type, Long targetId) {
//        if (type.equals("게시물")) {
//            Board board = boardRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물 ID"));
//            board.decrementLikesCount();
//            boardRepository.save(board);
//        } else if (type.equals("댓글")) {
//            Reply reply = replyRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 ID"));
//            reply.decrementLikesCount();
//            replyRepository.save(reply);
//        }
//    }
