package com.luckyvicky.woosan.domain.messages.service;

import com.luckyvicky.woosan.domain.messages.dto.MessageDTO;

public interface MessageService {

    // 쪽지 전송
    Long add(MessageDTO messageDTO);
}
