package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchingBoardServiceImpl implements MatchingBoardService {

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public MatchingBoard createMatchingBoard(MatchingBoardRequestDTO requestDTO){

        Member member = memberRepository.findById(requestDTO.getMemberId()).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        if (requestDTO.getMatchingType() == 1) {
            // 정기 모임 제약 조건 확인
            checkRegularyMeetingConstraints(member);
        } else if (requestDTO.getMatchingType() == 2) {
            // 번개 모임 제약 조건 확인
            checkTemporaryMeetingConstraints(member);
            if (!requestDTO.getMeetDate().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
                throw new IllegalArgumentException("번개 모임은 당일 날짜로만 생성할 수 있습니다.");
            }
        } else if (requestDTO.getMatchingType() == 3) {
            // 셀프 소개팅 제약 조건 확인
            checkSelfMeetingConstraints(member);
        }

        // MatchingBoard 객체를 생성하고 요청 DTO의 값을 설정합니다.
        MatchingBoard matchingBoard = MatchingBoard.builder()
                .member(member)
                .matchingType(requestDTO.getMatchingType())
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .placeName(requestDTO.getPlaceName())
                .locationX(requestDTO.getLocationX())
                .locationY(requestDTO.getLocationY())
                .address(requestDTO.getAddress())
                .meetDate(requestDTO.getMeetDate())
                .tag(requestDTO.getTag())
                .headCount(requestDTO.getHeadCount())
                .regDate(LocalDateTime.now())
                .views(0)
                .isDeleted(false)
                .build();

        return matchingBoardRepository.save(matchingBoard);
    }

    private void checkRegularyMeetingConstraints(Member member) {
        List<MatchingBoard> userRegularBoards = matchingBoardRepository.findByMemberAndMatchingType(member, 1);
        if (!userRegularBoards.isEmpty()) {
            throw new IllegalArgumentException("정기 모임은 한 개만 생성할 수 있습니다.");
        }
    }

    private void checkTemporaryMeetingConstraints(Member member) {
        List<MatchingBoard> existingLightningBoards = matchingBoardRepository.findByMemberAndMatchingTypeAndMeetDateBetween(
                member,
                2,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()
        );
        if (!existingLightningBoards.isEmpty()) {
            throw new IllegalArgumentException("당일 번개 모임은 한 개만 생성할 수 있습니다.");
        }
    }

    private void checkSelfMeetingConstraints(Member member) {
        List<MatchingBoard> userSelfDatingBoards = matchingBoardRepository.findByMemberAndMatchingType(member, 3);
        if (!userSelfDatingBoards.isEmpty()) {
            throw new IllegalArgumentException("셀프 소개팅 게시물은 한 개만 생성할 수 있습니다.");
        }
    }

    //매일 자정에 번개 모임 자동 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupTemporaryBoards() {
        matchingBoardRepository.deleteByMatchingTypeAndMeetDateBefore(2, LocalDateTime.now());
    }
}