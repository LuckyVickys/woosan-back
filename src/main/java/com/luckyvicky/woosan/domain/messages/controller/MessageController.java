package com.luckyvicky.woosan.domain.messages.controller;

import com.luckyvicky.woosan.domain.messages.dto.MessageDTO;
import com.luckyvicky.woosan.domain.messages.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    // 쪽지 작성
    @PostMapping("/add")
    public ResponseEntity<Long> register(@RequestBody MessageDTO messageDTO) {
        Long messageId = messageService.add(messageDTO);
        return ResponseEntity.ok(messageId);
    }
}
