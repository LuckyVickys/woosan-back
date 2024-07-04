package com.luckyvicky.woosan.domain.board.projection;

import java.time.LocalDateTime;

public interface IBoardMember {
    Long getId();
    String getTitle();
    String getContent();
    LocalDateTime getRegDate();
    int getViews();
    int getLikesCount();
    String getCategoryName();


    MemberInfo getWriter();

}
