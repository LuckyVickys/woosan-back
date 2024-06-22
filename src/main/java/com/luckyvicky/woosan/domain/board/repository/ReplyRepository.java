package com.luckyvicky.woosan.domain.board.repository;

import com.luckyvicky.woosan.domain.board.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Page<Reply> findByParentIdIsNull(Pageable pageable);

    Page<Reply> findByParentId(Long parentId, Pageable pageable);
}
