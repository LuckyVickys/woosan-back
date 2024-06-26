package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingBoardRepository extends JpaRepository<MatchingBoard, Long> {
}
