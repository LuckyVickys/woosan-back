package com.luckyvicky.woosan.domain.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MailDTO {
    private String email;
    private String title;
    private String message;
}
