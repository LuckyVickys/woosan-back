package com.luckyvicky.woosan.domain.board.projection;

import java.time.LocalDateTime;

public interface IReply {
    Long getId();
    Long getBoardId();
    String getContent();
    LocalDateTime getRegDate();
    Long parentId();
    int getLikesCount();

    MemberInfo getWriter();
}
