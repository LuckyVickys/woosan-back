package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.mapper.MatchingBoardMapper;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import com.luckyvicky.woosan.domain.member.repository.MemberProfileRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingBoardServiceImpl implements MatchingBoardService {

    private final MatchingBoardRepository matchingBoardRepository;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final MatchingBoardMapper matchingBoardMapper;

    // 모든 매칭 게시글을 가져오는 메서드
    @Override
    public List<MatchingBoardResponseDTO> getAllMatching() {
        return matchingBoardRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // 특정 타입의 매칭 게시글을 가져오는 메서드
    @Override
    public List<MatchingBoardResponseDTO> getMatchingByType(int matchingType) {
        return matchingBoardRepository.findByMatchingType(matchingType).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // 매칭 게시글을 생성하는 메서드
    @Override
    @Transactional
    public MatchingBoardResponseDTO createMatchingBoard(MatchingBoardRequestDTO requestDTO) {
        // 회원 정보 조회
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 매칭 타입에 따라 각각의 모임 생성 메소드 호출
        MatchingBoard matchingBoard;

        switch (requestDTO.getMatchingType()) {
            case 1:
                matchingBoard = createRegularlyBoard(member, requestDTO);
                break;
            case 2:
                matchingBoard = createTemporaryBoard(member, requestDTO);
                break;
            case 3:
                matchingBoard = createSelfBoard(member, requestDTO);
                break;
            default:
                throw new IllegalArgumentException("잘못된 매칭 타입입니다.");
        }

        return mapToResponseDTO(matchingBoard);
    }

    // 정기 모임 생성
    private MatchingBoard createRegularlyBoard(Member member, MatchingBoardRequestDTO requestDTO) {
        checkRegularlyConstraints(member); // 정기 모임 제약 조건 확인

        // MatchingBoard 객체 생성 및 설정
        MatchingBoard matchingBoard = matchingBoardMapper.toEntity(requestDTO).toBuilder()
                .member(member)
                .regDate(LocalDateTime.now())
                .views(0)
                .isDeleted(false)
                .build();

        return matchingBoardRepository.save(matchingBoard); // DB에 저장
    }

    // 번개 모임 생성
    private MatchingBoard createTemporaryBoard(Member member, MatchingBoardRequestDTO requestDTO) {
        checkTemporaryConstraints(member); // 번개 모임 제약 조건 확인

        // 번개 모임은 당일 날짜로만 생성 가능
        if (!requestDTO.getMeetDate().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            throw new IllegalArgumentException("번개 모임은 당일 날짜로만 생성할 수 있습니다.");
        }

        // MatchingBoard 객체 생성 및 설정
        MatchingBoard matchingBoard = matchingBoardMapper.toEntity(requestDTO).toBuilder()
                .member(member)
                .regDate(LocalDateTime.now())
                .views(0)
                .isDeleted(false)
                .build();

        return matchingBoardRepository.save(matchingBoard); // DB에 저장
    }

    // 셀프 소개팅 생성
    private MatchingBoard createSelfBoard(Member member, MatchingBoardRequestDTO requestDTO) {
        checkSelfConstraints(member); // 셀프 소개팅 제약 조건 확인

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
        MatchingBoard matchingBoard = matchingBoardMapper.toEntity(requestDTO).toBuilder()
                .member(member)
                .profile(profile)
                .regDate(LocalDateTime.now())
                .views(0)
                .isDeleted(false)
                .build();

        return matchingBoardRepository.save(matchingBoard); // DB에 저장
    }

    // 특정 매칭 게시글을 ID로 가져오는 메서드
    @Override
    public MatchingBoardResponseDTO getMatchingBoardById(Long id) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드가 존재하지 않습니다."));
        return mapToResponseDTO(matchingBoard);
    }

    // 매칭 보드 엔티티를 업데이트하는 메서드
    @Override
    @Transactional
    public MatchingBoardResponseDTO updateMatchingBoard(Long id, MatchingBoardRequestDTO requestDTO) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드가 존재하지 않습니다."));

        // 작성자 확인
        if (!matchingBoard.getMember().getId().equals(requestDTO.getMemberId())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        MatchingBoard updatedMatchingBoard = updateMatchingBoardEntity(matchingBoard, requestDTO);
        return mapToResponseDTO(matchingBoardRepository.save(updatedMatchingBoard));
    }

    // 특정 매칭 게시글을 삭제하는 메서드
    @Override
    @Transactional
    public void deleteMatchingBoard(Long id, Long memberId) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드가 존재하지 않습니다."));

        // 작성자 확인
        if (!matchingBoard.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        matchingBoardRepository.delete(matchingBoard);
    }

    // 정기 모임 제약 조건 확인
    private void checkRegularlyConstraints(Member member) {
        List<MatchingBoard> regularlyBoards = matchingBoardRepository.findByMemberAndMatchingType(member, 1);
        if (!regularlyBoards.isEmpty()) {
            throw new IllegalArgumentException("정기 모임은 한 개만 생성할 수 있습니다.");
        }
    }

    // 번개 모임 제약 조건 확인
    private void checkTemporaryConstraints(Member member) {
        List<MatchingBoard> temporaryBoards = matchingBoardRepository.findByMemberAndMatchingTypeAndMeetDateBetween(
                member,
                2,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()
        );
        if (!temporaryBoards.isEmpty()) {
            throw new IllegalArgumentException("당일 번개 모임은 한 개만 생성할 수 있습니다.");
        }
    }

    // 셀프 소개팅 제약 조건 확인
    private void checkSelfConstraints(Member member) {
        List<MatchingBoard> selfBoards = matchingBoardRepository.findByMemberAndMatchingType(member, 3);
        if (!selfBoards.isEmpty()) {
            throw new IllegalArgumentException("셀프 소개팅 게시물은 한 개만 생성할 수 있습니다.");
        }
    }

    // 매칭 보드 엔티티를 업데이트하는 메서드
    private MatchingBoard updateMatchingBoardEntity(MatchingBoard matchingBoard, MatchingBoardRequestDTO requestDTO) {
        MatchingBoard.MatchingBoardBuilder builder = matchingBoardMapper.toEntity(requestDTO).toBuilder()
                .id(matchingBoard.getId())
                .member(matchingBoard.getMember())
                .regDate(matchingBoard.getRegDate())
                .views(matchingBoard.getViews())
                .isDeleted(matchingBoard.getIsDeleted());

        // 셀프 소개팅인 경우 프로필 업데이트
        if (matchingBoard.getMatchingType() == 3) {
            MemberProfile existingProfile = matchingBoard.getProfile();
            MemberProfile updatedProfile = MemberProfile.builder()
                    .id(existingProfile.getId())
                    .member(existingProfile.getMember())
                    .phone(existingProfile.getPhone())
                    .location(requestDTO.getLocation())
                    .introduce(requestDTO.getIntroduce())
                    .mbti(requestDTO.getMbti())
                    .gender(requestDTO.getGender())
                    .age(requestDTO.getAge())
                    .height(requestDTO.getHeight())
                    .build();
            memberProfileRepository.save(updatedProfile);
            builder.profile(updatedProfile);
        }

        return builder.build();
    }

    // 매칭 보드 엔티티를 DTO로 변환하는 메서드
    private MatchingBoardResponseDTO mapToResponseDTO(MatchingBoard matchingBoard) {
        MatchingBoardResponseDTO responseDTO = matchingBoardMapper.toResponseDTO(matchingBoard);
        if (matchingBoard.getMatchingType() == 3 && matchingBoard.getProfile() != null) {
            MemberProfile profile = matchingBoard.getProfile();
            responseDTO = responseDTO.toBuilder()
                    .location(profile.getLocation())
                    .introduce(profile.getIntroduce())
                    .mbti(profile.getMbti())
                    .gender(profile.getGender())
                    .age(profile.getAge())
                    .height(profile.getHeight())
                    .build();
        }
        return responseDTO;
    }

    // 매일 자정에 번개 모임 자동 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupTemporaryBoards() {
        matchingBoardRepository.deleteByMatchingTypeAndMeetDateBefore(2, LocalDateTime.now());
    }
}
