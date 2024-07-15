package com.luckyvicky.woosan.domain.messages.dto;

import com.luckyvicky.woosan.domain.member.entity.Member;

import java.time.LocalDateTime;

public class MessageDTO {

    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime regDate;
    private Boolean delBySender;
    private Boolean delByReceiver;
}
