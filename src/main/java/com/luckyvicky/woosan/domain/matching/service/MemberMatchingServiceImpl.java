package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class MemberMatchingServiceImpl implements MemberMatchingService {

    @Autowired
    private MemberMatchingRepository memberMatchingRepository;

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberMatching createMatching(MemberMatchingRequestDTO requestDTO) {
        // 회원이 생성한 모임이 있는지 확인
        if (memberMatchingRepository.existsByMemberId(requestDTO.getMemberId())) {
            throw new IllegalArgumentException("하나의 모임만 생성할 수 있습니다.");
        }

        // 회원이 가입한 모임 수 확인
        if (memberMatchingRepository.countByMemberId(requestDTO.getMemberId()) >= 1) {
            throw new IllegalArgumentException("최대 1개의 모임까지 가입할 수 있습니다.");
        }

        // 매칭 보드와 회원 객체 가져오기
        MatchingBoard matchingBoard = matchingBoardRepository.findById(requestDTO.getMatchingBoardId())
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드가 존재하지 않습니다."));
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // MemberMatching 객체 생성 및 설정
        MemberMatching matching = new MemberMatching();
        matching.setMatchingBoard(matchingBoard);
        matching.setMember(member);
        matching.setIsAccepted(false);
        matching.setIsManaged(false);

        return memberMatchingRepository.save(matching);
    }

    @Override
    @Transactional
    public MemberMatching updateMatching(Long id, Boolean isAccepted) {
        MemberMatching matching = memberMatchingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("매칭을 찾을 수 없습니다."));
        matching.setIsAccepted(isAccepted);
        return memberMatchingRepository.save(matching);
    }
}
