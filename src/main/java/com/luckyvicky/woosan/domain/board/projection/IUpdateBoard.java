package com.luckyvicky.woosan.domain.board.projection;

import java.time.LocalDateTime;

public interface IUpdateBoard {
    Long getId();
    String getTitle();
    String getContent();
    LocalDateTime getRegDate();
    String getCategoryName();

    MemberInfo getWriter();
}

