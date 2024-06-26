package com.luckyvicky.woosan.domain.likes.service;

import org.springframework.transaction.annotation.Transactional;

public interface LikesService {

    @Transactional
    void toggleLike(Long memberId, String type, Long targetId);
}
