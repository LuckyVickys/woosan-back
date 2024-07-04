package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.RouletteDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
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

    @Override
    public void updateMemberLevel(RouletteDTO rouletteDTO) {
        Member member = memberRepository.findById(rouletteDTO.getMemberId())
                .orElseThrow(()-> new IllegalArgumentException("Member not found"));

        Long points = member.getPoint();
        if (points >= 1000) {
            member.setLevel(MemberType.Level.LEVEL_5);
        } else if (points >= 800) {
            member.setLevel(MemberType.Level.LEVEL_4);
        } else if (points >= 600) {
            member.setLevel(MemberType.Level.LEVEL_3);
        } else if (points >= 400) {
            member.setLevel(MemberType.Level.LEVEL_2);
        } else {
            member.setLevel(MemberType.Level.LEVEL_1);
        }

        memberRepository.save(member);
    }
}
