package com.luckyvicky.woosan.domain.board.projection;

import java.time.LocalDateTime;

public interface IMyBoard {
    Long getId();
    String getCategoryName();
    String getTitle();
    int getReplyCount();
    LocalDateTime getRegDate();
    int getViews();
    int getLikesCount();

}
