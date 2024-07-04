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

import java.util.List;

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

        //회원이 가입한 모임 수 확인
        if (memberMatchingRepository.countByMemberId(requestDTO.getMemberId()) >= 1) {
            throw new IllegalArgumentException("최대 1개의 모임까지 가입할 수 있습니다.");
        }

        //매칭 보드와 회원 객체 가져오기
        MatchingBoard matchingBoard = matchingBoardRepository.findById(requestDTO.getMatchingBoardId())
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드가 존재하지 않습니다."));
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // MemberMatching 객체 생성 및 설정
        MemberMatching matching = MemberMatching.builder()
                .matchingBoard(matchingBoard)
                .member(member)
                .isAccepted(false)
                .isManaged(false)
                .build();

        return memberMatchingRepository.save(matching);

    }


    @Override
    @Transactional
    public MemberMatching updateMatching(Long id, Boolean isAccepted) {
        MemberMatching matching = memberMatchingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("매칭을 찾을 수 없습니다."));

        // 매칭 객체 업데이트
        matching = MemberMatching.builder()
                .id(matching.getId())
                .matchingBoard(matching.getMatchingBoard())
                .member(matching.getMember())
                .isAccepted(isAccepted)
                .isManaged(matching.getIsManaged())
                .build();
        return memberMatchingRepository.save(matching);
    }

    @Override
    @Transactional
    public void leaveMatching(Long id, Long memberId) {
        MemberMatching matching = memberMatchingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("매칭을 찾을 수 없습니다."));

        // 탈퇴하려는 사람이 모임원인지 확인
        if (!matching.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("모임원만 탈퇴할 수 있습니다.");
        }

        memberMatchingRepository.delete(matching);
    }

    @Override
    @Transactional
    public void kickMember(Long matchingBoardId, Long memberId) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(matchingBoardId)
                .orElseThrow(() -> new RuntimeException("매칭 보드를 찾을 수 없습니다."));

        // 매칭 보드를 생성한 회원만 강퇴 가능
        Member admin = matchingBoard.getMember();

        MemberMatching matching = memberMatchingRepository.findByMatchingBoardIdAndMemberId(matchingBoardId, memberId)
                .orElseThrow(() -> new RuntimeException("회원이 해당 매칭 보드에 없습니다."));

        // 강퇴하려는 사람이 모임장인지 확인
        if (!matchingBoard.getMember().getId().equals(admin.getId())) {
            throw new IllegalArgumentException("모임장만 회원을 강퇴할 수 있습니다.");
        }

        memberMatchingRepository.delete(matching);
    }

    @Override
    public List<MemberMatching> getMembersByMatchingBoardId(Long matchingBoardId) {
        return memberMatchingRepository.findByMatchingBoardId(matchingBoardId);
    }
}
