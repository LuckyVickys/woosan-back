package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingBoardReplyRepository extends JpaRepository<MatchingBoardReply, Long> {
    List<MatchingBoardReply> findByMatchingId(Long matchingId);
}
