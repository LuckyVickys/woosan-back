package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.RouletteDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouletteServiceImpl implements RouletteService {
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void updateMemberPoints(RouletteDTO rouletteDTO){
        Member member = memberRepository.findById(rouletteDTO.getMemberId())
                .orElseThrow(()-> new IllegalArgumentException("Member not found"));

        member.addPoint(rouletteDTO.getPoint().intValue());

        memberRepository.save(member);
    }

}
