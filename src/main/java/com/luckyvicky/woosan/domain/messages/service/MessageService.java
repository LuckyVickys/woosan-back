package com.luckyvicky.woosan.domain.messages.service;

import com.luckyvicky.woosan.domain.messages.dto.MessageAddDTO;

public interface MessageService {

    // 쪽지 전송
    Long add(MessageAddDTO messageAddDTO);
}
