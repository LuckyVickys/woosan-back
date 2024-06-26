package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberMatchingServiceImpl implements MemberMatchingService{

    @Autowired
    private MemberMatchingRepository memberMatchingRepository;

    @Override
    public MemberMatching createMatching(MemberMatchingRequestDTO requestDTO){
        //회원이 생성한 모임이 있는지 확인
        if(memberMatchingRepository.existsByMemberId(requestDTO.getMemberId())){
            throw new IllegalArgumentException("하나의 모임만 생성할 수 있습니다.");
        }
        //회원이 가입한 모임 수 확인
        if(memberMatchingRepository.countByMemberId(requestDTO.getMemberId())>=1){
            throw new IllegalArgumentException("최대 1개의 모임까지 가입할 수 있습니다.");
        }
        MemberMatching matching = new MemberMatching();
        matching.setMatchingId(requestDTO.getMatchingId());
        matching.setMemberId(requestDTO.getMemberId());
        matching.setIsAccepted(false);
        matching.setIsManaged(false);
        return memberMatchingRepository.save(matching);
    }

    @Override
    public MemberMatching updateMatching(Long id, Boolean isAccepted) {
        MemberMatching matching = memberMatchingRepository.findById(id).orElseThrow(() -> new RuntimeException("매칭을 찾을 수 없습니다."));
        matching.setIsAccepted(isAccepted);
        return memberMatchingRepository.save(matching);

    }
}
