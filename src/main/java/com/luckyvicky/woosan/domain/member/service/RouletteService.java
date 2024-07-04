package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.RouletteDTO;

public interface RouletteService {
    void updateMemberPoints(RouletteDTO rouletteDTO);

    void updateMemberLevel(RouletteDTO rouletteDTO);
}
