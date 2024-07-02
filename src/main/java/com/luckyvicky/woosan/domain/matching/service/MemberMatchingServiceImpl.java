package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberMatchingServiceImpl implements MemberMatchingService {

    private final MemberMatchingRepository memberMatchingRepository;
    private final MatchingBoardRepository matchingBoardRepository;
    private final MemberRepository memberRepository;

    // 매칭 요청을 생성하는 메서드
    @Override
    public MemberMatching applyMatching(MemberMatchingRequestDTO requestDTO) {
        Long memberId = requestDTO.getMemberId();
        Long matchingBoardId = requestDTO.getMatchingBoardId();

        // 매칭 보드와 회원 객체 가져오기
        MatchingBoard matchingBoard = matchingBoardRepository.findById(matchingBoardId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드가 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 매칭 타입에 따른 조건 확인
        int type = matchingBoard.getMatchingType();

        if (type == 1) { // 정기 모임
            if (memberMatchingRepository.countByMemberIdAndType(memberId, 1) >= 2) {
                throw new IllegalArgumentException("정기 모임은 최대 2개까지 유지할 수 있습니다.");
            }
            if (memberMatchingRepository.countPendingByMemberIdAndType(memberId, 1) >= 3) {
                throw new IllegalArgumentException("정기 모임에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        } else if (type == 2) { // 번개 모임
            if (memberMatchingRepository.countByMemberIdAndType(memberId, 2) >= 1) {
                throw new IllegalArgumentException("번개 모임은 최대 1개까지 유지할 수 있습니다.");
            }
            if (memberMatchingRepository.countPendingByMemberIdAndType(memberId, 2) >= 3) {
                throw new IllegalArgumentException("번개 모임에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        } else if (type == 3) { // 셀프 소개팅
            if (memberMatchingRepository.countByMemberIdAndType(memberId, 3) >= 3) {
                throw new IllegalArgumentException("셀프 소개팅은 최대 3개까지 유지할 수 있습니다.");
            }
            if (memberMatchingRepository.countPendingByMemberIdAndType(memberId, 3) >= 3) {
                throw new IllegalArgumentException("셀프 소개팅에 대한 가입 대기 중인 신청은 최대 3개까지 가능합니다.");
            }
        }

        // MemberMatching 객체 생성 및 설정
        MemberMatching matching = MemberMatching.builder()
                .matchingBoard(matchingBoard)
                .member(member)
                .isAccepted(false)
                .isManaged(false)
                .build();

        return memberMatchingRepository.save(matching);
    }

    // 매칭 요청을 수락 또는 거부하는 메서드
    @Override
    @Transactional
    public MemberMatching updateMatching(Long id, Boolean isAccepted) {
        MemberMatching existingMatching = memberMatchingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("매칭을 찾을 수 없습니다."));

        // 빌더 패턴을 사용하여 객체 업데이트
        MemberMatching updatedMatching = existingMatching.toBuilder()
                .isAccepted(isAccepted)
                .build();

        if (isAccepted) {
            // 가입이 수락된 경우, 해당 회원의 같은 타입의 다른 대기 중인 가입 요청을 삭제
            int type = existingMatching.getMatchingBoard().getMatchingType();
            List<MemberMatching> pendingRequests = memberMatchingRepository.findPendingByMemberIdAndType(existingMatching.getMember().getId(), type);
            memberMatchingRepository.deleteAll(pendingRequests);
        }

        return memberMatchingRepository.save(updatedMatching);
    }

    // 매칭에서 탈퇴하는 메서드
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

    // 매칭에서 특정 회원을 강퇴하는 메서드
    @Override
    @Transactional
    public void kickMember(Long boardId, Long memberId) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("매칭 보드를 찾을 수 없습니다."));

        // 매칭 보드를 생성한 회원만 강퇴 가능
        Member admin = matchingBoard.getMember();

        MemberMatching matching = memberMatchingRepository.findByBoardIdAndMemberId(boardId, memberId)
                .orElseThrow(() -> new RuntimeException("회원이 해당 매칭 보드에 없습니다."));

        // 강퇴하려는 사람이 모임장인지 확인
        if (!matchingBoard.getMember().getId().equals(admin.getId())) {
            throw new IllegalArgumentException("모임장만 회원을 강퇴할 수 있습니다.");
        }

        memberMatchingRepository.delete(matching);
    }

    // 특정 매칭 보드에 속한 모든 회원을 가져오는 메서드
    @Override
    public List<MemberMatching> getMembersByMatchingBoardId(Long boardId) {
        return memberMatchingRepository.findByBoardId(boardId);
    }

    // 특정 매칭 보드에 대한 가입 대기 중인 요청들을 가져오는 메서드
    @Override
    public List<MemberMatching> getPendingRequestsByBoardId(Long boardId) {
        return memberMatchingRepository.findPendingByBoardId(boardId);
    }

    // 매칭 대기를 취소하는 메서드
    @Override
    @Transactional
    public void cancelMatchingRequest(Long matchingId, Long memberId) {
        MemberMatching matching = memberMatchingRepository.findById(matchingId)
                .orElseThrow(() -> new RuntimeException("매칭 요청을 찾을 수 없습니다."));

        // 취소하려는 사람이 요청자인지 확인
        if (!matching.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인만 요청을 취소할 수 있습니다.");
        }

        memberMatchingRepository.delete(matching);
    }
}
