package com.luckyvicky.woosan.domain.member.dto;

import com.luckyvicky.woosan.domain.member.entity.MemberType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouletteDTO {

    private long memberId;
    private Long point;
    private MemberType.Level level;

}
