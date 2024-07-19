package com.luckyvicky.woosan.domain.board.projection;

import java.time.LocalDateTime;

public interface IBoardList {
    Long getId();
    String getTitle();
    LocalDateTime getRegDate();
    int getViews();
    int getLikesCount();
    String getCategoryName();
    int getReplyCount();


    MemberInfo getWriter();
}
