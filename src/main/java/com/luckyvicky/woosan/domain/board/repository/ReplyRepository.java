package com.luckyvicky.woosan.domain.board.repository;

import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.projection.IReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {


    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"board", "writer"})
    Page<IReply> findByBoardIdAndParentIdIsNull(Long boardId, Pageable pageable);

    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"board", "writer"})
    List<IReply> findByParentId(Long parentId);








    // <---------------repository Test----------------->
    Page<Reply> findByParentIdIsNull(Pageable pageable);
    Page<Reply> findByParentId(Long parentId, Pageable pageable);
    // <---------------repository Test----------------->
}
