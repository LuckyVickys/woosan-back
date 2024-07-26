package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.exception.MatchingException;
import com.luckyvicky.woosan.domain.matching.mapper.MemberMatchingMapper;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
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
    private final FileImgService fileImgService;
    private final MemberMatchingMapper memberMatchingMapper;

    // MemberMatching 데이터를 생성하는 메서드
    @Override
    public MemberMatchingResponseDTO createMemberMatching(MemberMatchingRequestDTO requestDTO) {
        try {
            MemberMatching memberMatching = memberMatchingMapper.toEntity(requestDTO);
            MemberMatching savedMatching = memberMatchingRepository.save(memberMatching);
            return memberMatchingMapper.toDto(savedMatching);
        } catch (Exception e) {
            throw new MatchingException("MemberMatching 데이터를 생성하는 중 오류가 발생했습니다.");
        }
    }

    // 매칭 요청 생성
    @Override
    public MemberMatchingResponseDTO applyMatching(MemberMatchingRequestDTO requestDTO) {
        Long memberId = requestDTO.getMemberId();
        Long matchingId = requestDTO.getMatchingId();

        // 매칭 보드와 회원 객체 가져오기
        MatchingBoard matchingBoard = matchingBoardRepository.findById(matchingId)
                .orElseThrow(() -> new MatchingException("매칭 보드가 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MatchingException("회원이 존재하지 않습니다."));

        // 중복 가입 방지
        boolean isAlreadyMember = memberMatchingRepository.findByMatchingBoard_IdAndMember_Id(matchingId, memberId).isPresent();
        if (isAlreadyMember) {
            throw new MatchingException("이미 이 매칭에 가입 요청을 보냈습니다.");
        }

        // 매칭 타입에 따른 조건 확인
        int type = matchingBoard.getMatchingType();
        validateMatchingConditions(memberId, type);

        // MemberMatching 객체 생성 및 설정
        MemberMatching matching = MemberMatching.builder()
                .matchingBoard(matchingBoard)
                .member(member)
                .isAccepted(null) // 대기 중 상태
                .isManaged(false)
                .build();

        matching = memberMatchingRepository.save(matching);

        // 응답 DTO 생성
        return memberMatchingMapper.toDto(matching);
    }

    // 매칭 상태 업데이트 (수락/거절)
    @Override
    @Transactional
    public MemberMatchingResponseDTO updateMatching(Long id, Boolean isAccepted) {
        MemberMatching existingMatching = memberMatchingRepository.findById(id)
                .orElseThrow(() -> new MatchingException("매칭을 찾을 수 없습니다."));

        // 빌더 패턴을 사용하여 isAccepted 필드를 업데이트합니다.
        MemberMatching updatedMatching = existingMatching.toBuilder()
                .isAccepted(isAccepted)
                .build();

        if (Boolean.TRUE.equals(isAccepted)) {
            handlePendingRequestsOnAcceptance(existingMatching);
        }

        memberMatchingRepository.save(updatedMatching);
        return memberMatchingMapper.toDto(updatedMatching);
    }

    // 모임원 탈퇴
    @Override
    @Transactional
    public void leaveMatching(Long matchingId, Long memberId) {
        MemberMatching matching = memberMatchingRepository.findByMatchingBoard_IdAndMember_Id(matchingId, memberId)
                .orElseThrow(() -> new MatchingException("매칭을 찾을 수 없습니다."));

        // 탈퇴하려는 사람이 모임원인지 확인
        if (!matching.getMember().getId().equals(memberId)) {
            throw new MatchingException("모임원만 탈퇴할 수 있습니다.");
        }

        memberMatchingRepository.delete(matching);
    }

    // 회원 강퇴
    @Override
    @Transactional
    public void kickMember(Long matchingId, Long memberId) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(matchingId)
                .orElseThrow(() -> new MatchingException("매칭 보드를 찾을 수 없습니다."));

        // 매칭 보드를 생성한 회원만 강퇴 가능
        Member admin = matchingBoard.getMember();

        MemberMatching matching = memberMatchingRepository.findByMatchingBoard_IdAndMember_Id(matchingId, memberId)
                .orElseThrow(() -> new MatchingException("회원이 해당 매칭 보드에 없습니다."));

        // 강퇴하려는 사람이 모임장인지 확인
        if (!matchingBoard.isManager(admin.getId())) {
            throw new MatchingException("모임장만 회원을 강퇴할 수 있습니다.");
        }

        // isAccepted를 false로 업데이트하여 강퇴 처리
        matching = matching.toBuilder()
                .isAccepted(false)
                .build();

        memberMatchingRepository.save(matching);
    }

    // 특정 보드의 모든 멤버 가져오기
    @Override
    public List<MemberMatchingResponseDTO> getMembersByMatchingBoardId(Long matchingId) {
        List<MemberMatching> members = memberMatchingRepository.findByMatchingBoard_Id(matchingId);
        return members.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 특정 보드의 대기 중인 요청 가져오기
    @Override
    public List<MemberMatchingResponseDTO> getPendingRequestsByBoardId(Long matchingId) {
        List<MemberMatching> pendingRequests = memberMatchingRepository.findByMatchingBoard_IdAndIsAccepted(matchingId, null);
        return pendingRequests.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 매칭 대기 취소
    @Override
    @Transactional
    public void cancelMatchingRequest(Long matchingId, Long memberId) {
        MemberMatching matching = memberMatchingRepository.findByMatchingBoard_IdAndMember_Id(matchingId, memberId)
                .orElseThrow(() -> new MatchingException("매칭 요청을 찾을 수 없습니다."));

        // 취소하려는 사람이 요청자인지 확인
        if (!matching.getMember().getId().equals(memberId)) {
            throw new MatchingException("본인만 요청을 취소할 수 있습니다.");
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
            throw new MatchingException("멤버 매칭 데이터 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 보드의 모든 멤버의 isAccepted 상태 업데이트
    @Override
    @Transactional
    public void updateIsAcceptedByMatchingBoardId(Long matchingId, Boolean isAccepted) {
        try {
            List<MemberMatching> memberMatchings = memberMatchingRepository.findByMatchingBoard_Id(matchingId);
            for (MemberMatching memberMatching : memberMatchings) {
                memberMatching = memberMatching.toBuilder()
                        .isAccepted(isAccepted)
                        .build();
            }
            memberMatchingRepository.saveAll(memberMatchings);
        } catch (Exception e) {
            throw new MatchingException("멤버 매칭 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 보드의 관리자 찾기
    @Override
    public Optional<MemberMatchingResponseDTO> findManagerByMatchingBoardId(Long matchingId) {
        return memberMatchingRepository.findByMatchingBoard_IdAndIsManaged(matchingId, true)
                .map(this::toDTO);
    }

    // memberMatching 보드 엔티티를 DTO로 변환하는 메서드
    private MemberMatchingResponseDTO toDTO(MemberMatching memberMatching) {
        Member member = memberRepository.findById(memberMatching.getMember().getId())
                .orElseThrow(() -> new MatchingException("회원 정보를 찾을 수 없습니다."));
        List<String> profileImageUrls = fileImgService.findFiles("member", member.getId());
        return MemberMatchingResponseDTO.builder()
                .id(memberMatching.getId())
                .matchingId(memberMatching.getMatchingBoard().getId())
                .memberId(member.getId())
                .isAccepted(memberMatching.getIsAccepted())
                .isManaged(memberMatching.getIsManaged())
                .nickname(member.getNickname())
                .profileImageUrl(profileImageUrls.isEmpty() ? null : profileImageUrls.get(0))
                .build();
    }

    // 매칭 조건을 검증하는 메서드
    private void validateMatchingConditions(Long memberId, int type) {
        long joinedMeetings = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, type, true);
        long createdMeetings = matchingBoardRepository.countByMember_IdAndMatchingType(memberId, type);
        long pendingRequests = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, type, null);

        // 디버깅을 위해 각 조건의 로그를 출력합니다.
        System.out.println("Joined Meetings: " + joinedMeetings);
        System.out.println("Created Meetings: " + createdMeetings);
        System.out.println("Pending Requests: " + pendingRequests);

        if (type == 1) { // 정기 모임
            if (joinedMeetings >= 2) {
                throw new MatchingException("정기 모임은 내가 가입한 모임을 합쳐서 최대 2개까지 유지할 수 있습니다.");
            }
            if (createdMeetings >= 2) {
                throw new MatchingException("정기 모임은 내가 만든 모임을 합쳐서 최대 2개까지 유지할 수 있습니다.");
            }
            if (pendingRequests >= 3) {
                throw new MatchingException("정기 모임에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        } else if (type == 2) { // 번개 모임
            if (joinedMeetings >= 1) {
                throw new MatchingException("번개는 내가 가입한 모임을 합쳐서 최대 1개까지 유지할 수 있습니다.");
            }
            if (createdMeetings >= 1) {
                throw new MatchingException("번개는 내가 만든 모임을 합쳐서 최대 1개까지 유지할 수 있습니다.");
            }
            if (pendingRequests >= 3) {
                throw new MatchingException("번개에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        } else if (type == 3) { // 셀프 소개팅
            if (joinedMeetings >= 3) {
                throw new MatchingException("셀프 소개팅은 내가 가입한 모임을 합쳐서 최대 3개까지 유지할 수 있습니다.");
            }
            if (createdMeetings >= 3) {
                throw new MatchingException("셀프 소개팅은 내가 만든 모임을 합쳐서 최대 3개까지 유지할 수 있습니다.");
            }
            if (pendingRequests >= 3) {
                throw new MatchingException("셀프 소개팅에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        }
    }

    // 수락 시 대기 중인 요청 처리 메서드
    private void handlePendingRequestsOnAcceptance(MemberMatching existingMatching) {
        int type = existingMatching.getMatchingBoard().getMatchingType();
        Long memberId = existingMatching.getMember().getId();
        long joinedMeetings = memberMatchingRepository.countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, type, true);
        long createdMeetings = matchingBoardRepository.countByMember_IdAndMatchingType(memberId, type);

        boolean shouldDeletePendingRequests = false;

        if (type == 1) { // 정기 모임
            if (joinedMeetings >= 2 || createdMeetings >= 2) {
                shouldDeletePendingRequests = true;
            }
        } else if (type == 2) { // 번개 모임
            if (joinedMeetings >= 1 || createdMeetings >= 1) {
                shouldDeletePendingRequests = true;
            }
        } else if (type == 3) { // 셀프 소개팅
            if (joinedMeetings >= 3 || createdMeetings >= 3) {
                shouldDeletePendingRequests = true;
            }
        }

        if (shouldDeletePendingRequests) {
            List<MemberMatching> pendingRequests = memberMatchingRepository.findByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(memberId, type, null);
            memberMatchingRepository.deleteAll(pendingRequests);
        }
    }
}
