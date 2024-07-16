package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.mapper.MemberMatchingMapper;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberMatchingServiceImpl implements MemberMatchingService {

    private final MemberMatchingRepository memberMatchingRepository;
    private final MatchingBoardRepository matchingBoardRepository;
    private final MemberRepository memberRepository;
    private final MemberMatchingMapper memberMatchingMapper;

    // MemberMatching 데이터를 생성하는 메서드
    @Override
    public MemberMatchingResponseDTO createMemberMatching(MemberMatchingRequestDTO requestDTO) {
        try {
            MemberMatching memberMatching = memberMatchingMapper.toEntity(requestDTO);
            MemberMatching savedMatching = memberMatchingRepository.save(memberMatching);
            return memberMatchingMapper.toDto(savedMatching);
        } catch (Exception e) {
            throw new RuntimeException("MemberMatching 데이터를 생성하는 중 오류가 발생했습니다.", e);
        }
    }


    // 매칭 요청을 생성하는 메서드
    @Override
    public MemberMatchingResponseDTO applyMatching(MemberMatchingRequestDTO requestDTO) {
        Long memberId = requestDTO.getMemberId();
        Long matchingId = requestDTO.getMatchingId();

        // 매칭 보드와 회원 객체 가져오기
        var matchingBoard = matchingBoardRepository.findById(matchingId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드가 존재하지 않습니다."));
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 중복 가입 방지
        boolean isAlreadyMember = memberMatchingRepository.findByMatchingBoard_IdAndMember_Id(matchingId, memberId).isPresent();
        if (isAlreadyMember) {
            throw new IllegalArgumentException("이미 이 매칭에 가입 요청을 보냈습니다.");
        }

        // 매칭 타입에 따른 조건 확인
        int type = matchingBoard.getMatchingType();

        if (type == 1) { // 정기 모임
            long joinedMeetings = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, 1, true);
            long createdMeetings = matchingBoardRepository.countByMember_IdAndMatchingType(memberId, 1);
            long pendingRequests = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, 1, null);

            if (joinedMeetings + createdMeetings >= 2) {
                throw new IllegalArgumentException("정기 모임은 내가 만든 것과 가입한 것을 합쳐서 최대 2개까지 유지할 수 있습니다.");
            }
            if (pendingRequests >= 3) {
                throw new IllegalArgumentException("정기 모임에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        } else if (type == 2) { // 번개 모임
            long joinedMeetings = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, 2, true);
            long createdMeetings = matchingBoardRepository.countByMember_IdAndMatchingType(memberId, 2);
            long pendingRequests = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, 2, null);

            if (joinedMeetings + createdMeetings >= 1) {
                throw new IllegalArgumentException("번개는 내가 만든 것과 가입한 것을 합쳐서 최대 1개까지 유지할 수 있습니다.");
            }
            if (pendingRequests >= 3) {
                throw new IllegalArgumentException("번개에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        } else if (type == 3) { // 셀프 소개팅
            long joinedMeetings = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, 3, true);
            long createdMeetings = matchingBoardRepository.countByMember_IdAndMatchingType(memberId, 3);
            long pendingRequests = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, 3, null);

            if (joinedMeetings + createdMeetings >= 3) {
                throw new IllegalArgumentException("셀프 소개팅은 내가 만든 것과 가입한 것을 합쳐서 최대 3개까지 유지할 수 있습니다.");
            }
            if (pendingRequests >= 3) {
                throw new IllegalArgumentException("셀프 소개팅에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        }

        // MemberMatching 객체 생성 및 설정
        MemberMatching matching = memberMatchingMapper.toEntity(requestDTO)
                .toBuilder()
                .matchingBoard(matchingBoard)
                .member(member)
                .isAccepted(null) // 대기 중 상태
                .isManaged(false)
                .build();

        memberMatchingRepository.save(matching);

        // 응답 DTO 생성
        return memberMatchingMapper.toDto(matching);
    }

    // 매칭 상태를 업데이트하는 메서드
    @Override
    @Transactional
    public MemberMatchingResponseDTO updateMatching(Long id, Boolean isAccepted) {
        MemberMatching existingMatching = memberMatchingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("매칭을 찾을 수 없습니다."));

        // 매칭 상태 업데이트
        MemberMatching updatedMatching = existingMatching.toBuilder()
                .isAccepted(isAccepted)
                .build();

        if (Boolean.TRUE.equals(isAccepted)) {
            // 가입이 수락된 경우, 해당 회원의 같은 타입의 다른 대기 중인 가입 요청을 삭제
            int type = existingMatching.getMatchingBoard().getMatchingType();
            Long memberId = existingMatching.getMember().getId();
            long joinedMeetings = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, type, true);
            long createdMeetings = matchingBoardRepository.countByMember_IdAndMatchingType(memberId, type);

            boolean shouldDeletePendingRequests = false;

            if (type == 1) { // 정기 모임
                if (joinedMeetings + createdMeetings >= 2) {
                    shouldDeletePendingRequests = true;
                }
            } else if (type == 2) { // 번개 모임
                if (joinedMeetings + createdMeetings >= 1) {
                    shouldDeletePendingRequests = true;
                }
            } else if (type == 3) { // 셀프 소개팅
                if (joinedMeetings + createdMeetings >= 3) {
                    shouldDeletePendingRequests = true;
                }
            }

            if (shouldDeletePendingRequests) {
                List<MemberMatching> pendingRequests = memberMatchingRepository.findByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, type, null);
                memberMatchingRepository.deleteAll(pendingRequests);
            }
        }

        memberMatchingRepository.save(updatedMatching);

        // 응답 DTO 생성
        return memberMatchingMapper.toDto(updatedMatching);
    }

    // 모임에서 탈퇴하는 메서드
    @Override
    @Transactional
    public void leaveMatching(Long matchingId, Long memberId) {
        MemberMatching matching = memberMatchingRepository.findByMatchingBoard_IdAndMember_Id(matchingId, memberId)
                .orElseThrow(() -> new RuntimeException("매칭을 찾을 수 없습니다."));

        // 탈퇴하려는 사람이 모임원인지 확인
        if (!matching.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("모임원만 탈퇴할 수 있습니다.");
        }

        memberMatchingRepository.delete(matching);
    }

    // 매칭에서 특정 회원을 강퇴하는 메서드
    @Override
    @Transactional
    public void kickMember(Long matchingId, Long memberId) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(matchingId)
                .orElseThrow(() -> new RuntimeException("매칭 보드를 찾을 수 없습니다."));

        // 매칭 보드를 생성한 회원만 강퇴 가능
        Member admin = matchingBoard.getMember();

        MemberMatching matching = memberMatchingRepository.findByMatchingBoard_IdAndMember_Id(matchingId, memberId)
                .orElseThrow(() -> new RuntimeException("회원이 해당 매칭 보드에 없습니다."));

        // 강퇴하려는 사람이 모임장인지 확인
        if (!matchingBoard.isManager(admin.getId())) {
            throw new IllegalArgumentException("모임장만 회원을 강퇴할 수 있습니다.");
        }

        memberMatchingRepository.delete(matching);
    }

    // 특정 매칭 보드에 속한 모든 회원을 가져오는 메서드
    @Override
    public List<MemberMatchingResponseDTO> getMembersByMatchingBoardId(Long matchingId) {
        try {
            List<MemberMatching> members = memberMatchingRepository.findByMatchingBoard_Id(matchingId);
            return members.stream()
                    .map(memberMatchingMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("특정 매칭 보드에 속한 모든 회원을 가져오는 중 오류가 발생했습니다.", e);
        }
    }

    // 특정 매칭 보드에 대한 가입 대기 중인 요청들을 가져오는 메서드
    @Override
    public List<MemberMatchingResponseDTO> getPendingRequestsByBoardId(Long matchingId) {
        try {
            List<MemberMatching> pendingRequests = memberMatchingRepository.findByMatchingBoard_IdAndIsAccepted(matchingId, null);
            return pendingRequests.stream()
                    .map(memberMatchingMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("특정 매칭 보드에 대한 가입 대기 중인 요청들을 가져오는 중 오류가 발생했습니다.", e);
        }
    }

    // 매칭 대기를 취소하는 메서드
    @Override
    @Transactional
    public void cancelMatchingRequest(Long matchingId, Long memberId) {
        MemberMatching matching = memberMatchingRepository.findByMatchingBoard_IdAndMember_Id(matchingId, memberId)
                .orElseThrow(() -> new RuntimeException("매칭 요청을 찾을 수 없습니다."));

        // 취소하려는 사람이 요청자인지 확인
        if (!matching.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인만 요청을 취소할 수 있습니다.");
        }

        memberMatchingRepository.delete(matching);
    }

    // 특정 보드의 모든 멤버 매칭 데이터 삭제
    @Override
    @Transactional
    public void deleteAllMembersByMatchingBoardId(Long matchingId) {
        try {
            memberMatchingRepository.deleteByMatchingBoard_Id(matchingId);
        } catch (Exception e) {
            throw new RuntimeException("멤버 매칭 데이터 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 보드의 모든 멤버의 isAccepted 상태 업데이트
    @Override
    @Transactional
    public void updateIsAcceptedByMatchingBoardId(Long matchingId, Boolean isAccepted) {
        try {
            List<MemberMatching> memberMatchings = memberMatchingRepository.findByMatchingBoard_Id(matchingId);
            for (MemberMatching memberMatching : memberMatchings) {
                memberMatching.toBuilder()
                        .isAccepted(isAccepted)
                        .build();
            }
            memberMatchingRepository.saveAll(memberMatchings);
        } catch (Exception e) {
            throw new RuntimeException("멤버 매칭 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 보드의 관리자 찾기
    @Override
    public Optional<MemberMatchingResponseDTO> findManagerByMatchingBoardId(Long matchingId) {
        return memberMatchingRepository.findByMatchingBoard_IdAndIsManaged(matchingId, true)
                .map(memberMatchingMapper::toDto);
    }
}
