package com.luckyvicky.woosan.domain.messages.dto;

import com.luckyvicky.woosan.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime regDate;
    private Boolean delBySender;
    private Boolean delByReceiver;
}
