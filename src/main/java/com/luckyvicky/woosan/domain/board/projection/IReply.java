package com.luckyvicky.woosan.domain.board.projection;

import java.time.LocalDateTime;

public interface IReply {
    Long getId();
    Long getBoardId();
    String getContent();
    LocalDateTime regDate();
    Long parentId();
    int getLikes();


    MemberInfo getWriter();
}
