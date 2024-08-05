package com.luckyvicky.woosan.domain.board.projection;

import java.time.LocalDateTime;

public interface IMyReply {
    Long getId();
    Long getBoardId();
    String getContent();
    LocalDateTime getRegDate();
    int getLikesCount();

    BoardInfo getBoard();
}
