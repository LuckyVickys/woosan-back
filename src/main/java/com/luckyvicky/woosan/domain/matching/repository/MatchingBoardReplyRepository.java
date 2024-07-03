package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MatchingBoardReplyRepository extends JpaRepository<MatchingBoardReply, Long> {

    // 특정 매칭 보드의 부모 댓글을 페이지 단위로 조회
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"matchingBoard", "writer"})
    Page<MatchingBoardReply> findAllByMatchingBoardId(Long matchingId, Pageable pageable);

    // 특정 부모 댓글의 자식 댓글을 조회
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"matchingBoard", "writer"})
    List<MatchingBoardReply> findAllByParentId(Long parentId);
}
