package com.luckyvicky.woosan.domain.board.repository;

import com.luckyvicky.woosan.domain.board.entity.Board;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository  extends JpaRepository<Board, Long> {


    /**
     * <Test>
     * 단일 인터페이스 프로젝션
     */
//    @Transactional
//    <T> List<T> findAllProjectedBy(Class<T> className);


    /**
     * board-member 연관관계 인터페이스 프로젝션
     * 게시물 단건 조회
     */
    @Transactional
    @EntityGraph(attributePaths = {"writer"})
    <T> Optional<T> findById(Long id, Class<T> className);



    /**
     * 연관관계 인터페이스 프로젝션
     * 게시물 전체 조회
     */
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"writer"})
    <T> Page<T> findAllProjectedBy(Pageable pageable, Class<T> className);





    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"writer"})
    <T> Page<T> findAllProjectedByCategoryName(String categoryName, Pageable pageable, Class<T> type);

}

