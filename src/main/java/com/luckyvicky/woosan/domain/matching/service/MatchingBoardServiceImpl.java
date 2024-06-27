package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import com.luckyvicky.woosan.domain.member.repository.MemberProfileRepository;
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

    @Autowired
    private MemberProfileRepository memberProfileRepository;

    // 매칭 보드 생성
    @Override
    @Transactional
    public MatchingBoard createMatchingBoard(MatchingBoardRequestDTO requestDTO) {
        // 회원 정보 조회
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 매칭 타입에 따라 각각의 모임 생성 메소드 호출
        switch (requestDTO.getMatchingType()) {
            case 1:
                return createRegularMeetingBoard(member, requestDTO); // 정기 모임 생성
            case 2:
                return createTemporaryMeetingBoard(member, requestDTO); // 번개 모임 생성
            case 3:
                return createSelfIntroductionBoard(member, requestDTO); // 셀프 소개팅 생성
            default:
                throw new IllegalArgumentException("잘못된 매칭 타입입니다."); // 잘못된 매칭 타입 처리
        }
    }

    // 정기 모임 생성
    private MatchingBoard createRegularMeetingBoard(Member member, MatchingBoardRequestDTO requestDTO) {
        checkRegularyMeetingConstraints(member); // 정기 모임 제약 조건 확인

        // MatchingBoard 객체 생성 및 설정
        MatchingBoard matchingBoard = MatchingBoard.builder()
                .member(member)
                .matchingType(1)
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

        return matchingBoardRepository.save(matchingBoard); // DB에 저장
    }

    // 번개 모임 생성
    private MatchingBoard createTemporaryMeetingBoard(Member member, MatchingBoardRequestDTO requestDTO) {
        checkTemporaryMeetingConstraints(member); // 번개 모임 제약 조건 확인

        // 번개 모임은 당일 날짜로만 생성 가능
        if (!requestDTO.getMeetDate().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            throw new IllegalArgumentException("번개 모임은 당일 날짜로만 생성할 수 있습니다.");
        }

        // MatchingBoard 객체 생성 및 설정
        MatchingBoard matchingBoard = MatchingBoard.builder()
                .member(member)
                .matchingType(2)
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

        return matchingBoardRepository.save(matchingBoard); // DB에 저장
    }

    // 셀프 소개팅 생성
    private MatchingBoard createSelfIntroductionBoard(Member member, MatchingBoardRequestDTO requestDTO) {
        checkSelfMeetingConstraints(member); // 셀프 소개팅 제약 조건 확인

        // 프로필 정보를 가져오거나 새로 생성
        MemberProfile profile = memberProfileRepository.findByMemberId(requestDTO.getMemberId())
                .orElseGet(() -> {
                    MemberProfile newProfile = MemberProfile.builder()
                            .member(member)
                            .location(requestDTO.getLocation())
                            .introduce(requestDTO.getIntroduce())
                            .mbti(requestDTO.getMbti())
                            .gender(requestDTO.getGender())
                            .age(requestDTO.getAge())
                            .height(requestDTO.getHeight())
                            .build();
                    return memberProfileRepository.save(newProfile);
                });

        // MatchingBoard 객체 생성 및 설정
        MatchingBoard matchingBoard = MatchingBoard.builder()
                .member(member)
                .matchingType(3)
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
                .profile(profile) // 프로필 설정
                .build();

        return matchingBoardRepository.save(matchingBoard); // DB에 저장
    }

    // 정기 모임 제약 조건 확인
    private void checkRegularyMeetingConstraints(Member member) {
        List<MatchingBoard> userRegularBoards = matchingBoardRepository.findByMemberAndMatchingType(member, 1);
        if (!userRegularBoards.isEmpty()) {
            throw new IllegalArgumentException("정기 모임은 한 개만 생성할 수 있습니다.");
        }
    }

    // 번개 모임 제약 조건 확인
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

    // 셀프 소개팅 제약 조건 확인
    private void checkSelfMeetingConstraints(Member member) {
        List<MatchingBoard> userSelfDatingBoards = matchingBoardRepository.findByMemberAndMatchingType(member, 3);
        if (!userSelfDatingBoards.isEmpty()) {
            throw new IllegalArgumentException("셀프 소개팅 게시물은 한 개만 생성할 수 있습니다.");
        }
    }

    // 매일 자정에 번개 모임 자동 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupTemporaryBoards() {
        matchingBoardRepository.deleteByMatchingTypeAndMeetDateBefore(2, LocalDateTime.now());
    }
}
