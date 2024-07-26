package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.exception.MatchingException;
import com.luckyvicky.woosan.domain.matching.mapper.MatchingBoardMapper;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberProfileRepository;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingBoardServiceImpl implements MatchingBoardService {

    private final MatchingBoardRepository matchingBoardRepository;
    private final MemberMatchingRepository memberMatchingRepository;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final MatchingBoardMapper matchingBoardMapper;
    private final FileImgService fileImgService;
    private final MemberMatchingService memberMatchingService;
    private final RedisTemplate<String, String> redisTemplate;

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
                .orElseThrow(() -> new MatchingException("회원이 존재하지 않습니다."));

        // 회원의 레벨을 확인
        validateMemberLevel(member, requestDTO.getMatchingType());

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
                throw new MatchingException("잘못된 매칭 타입입니다.");
        }

        // 파일 업로드 및 DB 저장
        if (requestDTO.getImages() != null) {
            fileImgService.fileUploadMultiple("matchingBoard", matchingBoard.getId(), requestDTO.getImages());
        }

        // 매칭 보드 엔티티를 저장 후 DTO로 변환
        MatchingBoardResponseDTO responseDTO = mapToResponseDTO(matchingBoardRepository.save(matchingBoard));

        return responseDTO;
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
            throw new MatchingException("번개 모임은 당일 날짜로만 생성할 수 있습니다.");
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

    // 특정 사용자가 만든 매칭 게시글을 가져오는 메서드
    @Override
    public List<MatchingBoardResponseDTO> getMatchingBoardsByMemberId(Long memberId) {
        return matchingBoardRepository.findByMemberId(memberId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // 특정 매칭 게시글을 수정하는 메서드
    @Override
    @Transactional
    public MatchingBoardResponseDTO updateMatchingBoard(Long id, MatchingBoardRequestDTO requestDTO, List<MultipartFile> images) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(id)
                .orElseThrow(() -> new MatchingException("매칭 보드가 존재하지 않습니다."));

        // 작성자 확인
        if (!matchingBoard.getMember().getId().equals(requestDTO.getMemberId())) {
            throw new MatchingException("작성자만 수정할 수 있습니다.");
        }

        // 매칭 보드 엔티티 업데이트
        MatchingBoard updatedMatchingBoard = updateMatchingBoardEntity(matchingBoard, requestDTO);

        // 기존 파일 처리 및 새로운 파일 업로드
        updateBoardFiles(requestDTO, images, matchingBoard.getId());

        return mapToResponseDTO(matchingBoardRepository.save(updatedMatchingBoard));
    }

    // 특정 매칭 게시글을 삭제하는 메서드
    @Override
    @Transactional
    public void deleteMatchingBoard(Long id, Long memberId) {
        MatchingBoard matchingBoard = matchingBoardRepository.findById(id)
                .orElseThrow(() -> new MatchingException("매칭 보드가 존재하지 않습니다."));

        // 작성자 확인
        if (!matchingBoard.getMember().getId().equals(memberId)) {
            throw new MatchingException("작성자만 삭제할 수 있습니다.");
        }

        // 관련된 모든 member_matching 행 삭제
        memberMatchingService.updateIsAcceptedByMatchingBoardId(id, false);
        memberMatchingService.deleteAllMembersByMatchingBoardId(id);

        // 이미지 파일 삭제
        fileImgService.targetFilesDelete("matchingBoard", matchingBoard.getId());

        // 매칭 보드와 관련된 댓글도 함께 삭제
        matchingBoardRepository.delete(matchingBoard);
    }

    // 정기 모임 제약 조건 확인
    private void checkRegularlyConstraints(Member member) {
        List<MatchingBoard> regularlyBoards = matchingBoardRepository.findByMemberAndMatchingType(member, 1);
        if (!regularlyBoards.isEmpty()) {
            throw new MatchingException("정기 모임은 한 개만 생성할 수 있습니다.");
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
            throw new MatchingException("당일 번개 모임은 한 개만 생성할 수 있습니다.");
        }
    }

    // 셀프 소개팅 제약 조건 확인
    private void checkSelfConstraints(Member member) {
        List<MatchingBoard> selfBoards = matchingBoardRepository.findByMemberAndMatchingType(member, 3);
        if (!selfBoards.isEmpty()) {
            throw new MatchingException("셀프 소개팅 게시물은 한 개만 생성할 수 있습니다.");
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

    // 매칭 보드 파일 정보 갱신
    private void updateBoardFiles(MatchingBoardRequestDTO requestDTO, List<MultipartFile> images, Long boardId) {
        if (requestDTO.getFilePathUrl() == null) {
            fileImgService.targetFilesDelete("matchingBoard", boardId);
        } else {
            List<String> beforeFiles = fileImgService.findFiles("matchingBoard", boardId);
            List<String> afterFiles = requestDTO.getFilePathUrl();

            for (String beforeFile : beforeFiles) {
                if (!afterFiles.contains(beforeFile)) {
                    fileImgService.deleteS3FileByUrl(boardId, "matchingBoard", beforeFile);
                }
            }
        }

        if (images != null) {
            fileImgService.fileUploadMultiple("matchingBoard", boardId, images);
        }
    }

    // 매칭 보드 엔티티를 DTO로 변환하는 메서드
    private MatchingBoardResponseDTO mapToResponseDTO(MatchingBoard matchingBoard) {
        MatchingBoardResponseDTO responseDTO = matchingBoardMapper.toResponseDTO(matchingBoard);

        // 파일 경로 리스트를 가져옵니다.
        List<String> fileUrls = fileImgService.findFiles("matchingBoard", matchingBoard.getId());

        // 멤버 정보 조회
        Member member = memberRepository.findById(matchingBoard.getMember().getId())
                .orElseThrow(() -> new MatchingException("회원이 존재하지 않습니다."));

        // 프로필 이미지 파일 경로를 가져옵니다.
        List<String> profileImageUrls = fileImgService.findFiles("member", member.getId());

        // 파일 경로 리스트를 설정합니다.
        responseDTO = responseDTO.toBuilder()
                .filePathUrl(fileUrls)
                .profileImageUrl(profileImageUrls)
                .nickname(member.getNickname())
                .build();

        // 셀프소개팅 타입의 경우 추가 필드를 설정합니다.
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

    // 게시글 조회수를 계산하는 메서드
    @Override
    @Transactional
    public void increaseViewCount(Long boardId, Long memberId, Long writerId, HttpServletRequest request) {
        String redisKey;
        Duration duration;

        if (memberId != null) {
            // 로그인한 사용자인 경우
            if (memberId.equals(writerId)) {
                // 작성자인 경우 24시간 동안 유지
                redisKey = "viewedBoard_" + boardId + "_writer_" + memberId;
                duration = Duration.ofHours(24);
            } else {
                // 로그인한 일반 사용자인 경우 1분 동안 유지
                redisKey = "viewedBoard_" + boardId + "_user_" + memberId;
                duration = Duration.ofMinutes(1);
            }
        } else {
            // 로그인하지 않은 사용자인 경우 IP 주소를 기반으로 처리
            String ipAddress = getClientIpAddress(request);
            redisKey = "viewedBoard_" + boardId + "_ip_" + ipAddress;
            duration = Duration.ofMinutes(1);
        }

        String hasViewedStr = redisTemplate.opsForValue().get(redisKey);
        Boolean hasViewed = Boolean.valueOf(hasViewedStr);

        if (hasViewed == null || !hasViewed) {
            MatchingBoard board = matchingBoardRepository.findById(boardId)
                    .orElseThrow(() -> new MatchingException("매칭 보드를 찾을 수 없습니다."));

            // 조회수 증가
            board.incrementViews(); // 엔티티의 메서드를 사용
            matchingBoardRepository.save(board);

            // Redis에 조회 기록 저장
            redisTemplate.opsForValue().set(redisKey, "true", duration);
        }
    }

    // 회원의 레벨을 검증하는 메서드
    @Override
    public void validateMemberLevel(Member member, int matchingType) {
        switch (matchingType) {
            case 1: // 정기 모임
                if (member.getLevel().ordinal() < MemberType.Level.LEVEL_3.ordinal()) {
                    throw new MatchingException("정기 모임을 생성하려면 레벨 3 이상이어야 합니다.");
                }
                break;
            case 2: // 번개 및 셀프 소개팅
                if (member.getLevel().ordinal() < MemberType.Level.LEVEL_2.ordinal()) {
                    throw new MatchingException("번개와 셀프 소개팅을 생성하려면 레벨 2 이상이어야 합니다.");
                }
                break;
            default:
                throw new MatchingException("잘못된 매칭 타입입니다.");
        }
    }

    // IP 주소를 가져오는 메서드
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
