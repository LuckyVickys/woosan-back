package com.luckyvicky.woosan.domain.likes.repository;


import com.luckyvicky.woosan.domain.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByMemberIdAndTypeAndTargetId(Long memberId, String type, Long targetId);
}