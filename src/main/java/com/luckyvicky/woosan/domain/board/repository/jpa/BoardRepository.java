package com.luckyvicky.woosan.domain.board.repository.jpa;

import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.projection.IBoardList;

import com.luckyvicky.woosan.domain.board.projection.IBoardMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.luckyvicky.woosan.domain.board.projection.IMyBoard;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository  extends JpaRepository<Board, Long> {

    /**
     * 다이나믹 프로젝션
     * 게시물 단건 조회
     */
    @Transactional
    @EntityGraph(attributePaths = {"writer"})
    <T> Optional<T> findById(Long id, Class<T> className);

    @Transactional
    @EntityGraph(attributePaths = {"writer"})
    Page<IMyBoard> findByWriterIdAndIsDeletedFalse(Long memberId, Pageable pageable);

    /**
     * 다이나믹 프로젝션
     * 게시물 단건 조회
     */
    @Transactional
    @EntityGraph(attributePaths = {"writer"})
    Optional<IBoardMember> findByIdAndCategoryName(Long id, String categoryName);

    /**
     * 인터페이스 프로젝션
     * 게시물 전체 조회
     */
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"writer"})
    Page<IBoardList> findAllProjectedByCategoryNameNotAndIsDeletedFalseOrderByRegDateDesc(String categoryName, Pageable pageable);


    /**
     * 카테고리별 게시물 전체 조회
     */
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"writer"})
    Page<IBoardList> findAllProjectedByCategoryNameAndIsDeletedFalseOrderByRegDateDesc(String categoryName, Pageable pageable);


    /**
     * 공지사항 상단 고정
     * 최신 단건 게시물 조회
     */
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"writer"})
    Optional<IBoardList> findFirstByCategoryNameAndIsDeletedFalse(String categoryName);


    /**
     * 인기글 상단 고정
     * 인기글 상위 3개 게시물 조회
     */
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"writer"})
    List<IBoardList> findTop3ByIsDeletedFalseOrderByViewsDesc();


    /**
     * 공지사항 10개 게시물 조회 (메인페이지)
     *
     */
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"writer"})
    List<IBoardList> findTop5ProjectedByCategoryNameAndIsDeletedFalse(String categoryName);


    /**
     * 인기글 5개 게시물 조회 (메인페이지)
     */
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"writer"})
    List<IBoardList> findTop10ProjectedByIsDeletedFalseOrderByLikesCountDesc();


    @Transactional(readOnly = true)
    Page<IMyBoard> findByWriterId(Long writerId, Pageable pageable);


    /**
     * 내가 추천한 게시물 조회
     * */
    @Query("SELECT b " +
            "FROM Likes l JOIN Board b ON l.targetId = b.id " +
            "WHERE l.type = '게시물' AND l.member.id = :memberId")
    Page<Board> findLikedBoards(@Param("memberId") Long memberId, Pageable pageable);


}

