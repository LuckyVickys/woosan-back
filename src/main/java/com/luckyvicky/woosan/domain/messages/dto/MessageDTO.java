package com.luckyvicky.woosan.domain.messages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    private Long senderId;
    private Long receiverId;

    @NotBlank(message = "쪽지 내용은 필수 항목입니다.")
    @Size(min = 1, max = 100, message = "쪽지 내용은 1자 이상 100자 이하여야 합니다.")
    private String content;

    private LocalDateTime regDate;
    private Boolean delBySender;
    private Boolean delByReceiver;
}
