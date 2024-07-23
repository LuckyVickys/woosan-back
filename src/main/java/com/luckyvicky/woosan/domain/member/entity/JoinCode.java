package com.luckyvicky.woosan.domain.member.entity;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "joinCode", timeToLive = 60 * 5)
public class JoinCode {

    @Id
    private String code;
    @Indexed
    private String email;
}
