package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.entity.SocialType;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Log4j2
@SpringBootTest
public class MatchingBoardRepositoryTests {

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @Autowired
    private MatchingBoardService matchingBoardService;

    @Autowired
    private MemberRepository memberRepository;

    private List<Member> testMembers = new ArrayList<>();

    @BeforeEach
    public void setup() {
        // 테스트에 사용할 5명의 회원 엔티티 생성
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Member member = memberRepository.save(Member.builder()
                    .email("test" + System.currentTimeMillis() + i + "@woosan.com")
                    .nickname("testuser" + System.currentTimeMillis() + i)
                    .password("password")
                    .point(0L)
                    .memberType(MemberType.USER)
                    .level(MemberType.Level.valueOf("LEVEL_" + i))
                    .isActive(true)
                    .socialType(SocialType.NORMAL)
                    .build());
            testMembers.add(member);
        });
    }

    @Test
    public void testAddMatchingBoard() {
        // 5개의 서로 다른 매칭 보드 생성 (정기, 번개, 셀프 소개팅 각각 1개씩 생성)
        IntStream.rangeClosed(1, 5).forEach(i -> {
            int matchingType = i % 3 + 1; // 1, 2, 3 순서로 타입 지정
            MatchingBoard matchingBoard = MatchingBoard.builder()
                    .member(testMembers.get(i - 1)) // 회원 엔티티 설정
                    .matchingType(matchingType) // 매칭 타입 설정
                    .title("매칭 제목" + i)
                    .content("매칭 내용" + i)
                    .regDate(LocalDateTime.now())
                    .views(0)
                    .isDeleted(false)
                    .placeName("매칭 장소" + i)
                    .locationX(BigDecimal.valueOf(37.5665 + i * 0.01))
                    .locationY(BigDecimal.valueOf(126.9780 + i * 0.01))
                    .address("서울특별시" + i)
                    .meetDate(LocalDateTime.now().plusDays(i))
                    .tag("태그" + i)
                    .headCount(10 + i)
                    .build();

            log.info("matchingBoard_id: " + matchingBoardRepository.save(matchingBoard).getId());
        });
    }

    @Test
    public void testRead() {
        // 특정 ID로 매칭 보드를 읽어옴
        Long id = 1L;
        MatchingBoard matchingBoard = matchingBoardRepository.findById(id).orElse(null);
        log.info("--------------------");
        log.info(matchingBoard);
    }

    @Test
    public void testCreateRegularyMatchingBoard() {
        // 5개의 정기 모임 생성
        IntStream.rangeClosed(1, 5).forEach(i -> {
            MatchingBoardRequestDTO requestDTO = new MatchingBoardRequestDTO();
            requestDTO.setMemberId(testMembers.get(i - 1).getId()); // 회원 ID 설정
            requestDTO.setMatchingType(1); // 정기 모임
            requestDTO.setTitle("정기 모임 제목" + i);
            requestDTO.setContent("정기 모임 내용" + i);
            requestDTO.setPlaceName("장소" + i);
            requestDTO.setLocationX(BigDecimal.valueOf(37.5665 + i * 0.01));
            requestDTO.setLocationY(BigDecimal.valueOf(126.9780 + i * 0.01));
            requestDTO.setAddress("서울특별시" + i);
            requestDTO.setMeetDate(LocalDateTime.now().plusDays(i));
            requestDTO.setTag("태그" + i);
            requestDTO.setHeadCount(10 + i);

            // 정기 모임 생성 테스트
            MatchingBoard savedBoard = matchingBoardService.createMatchingBoard(requestDTO);
            assertThat(savedBoard).isNotNull();

            // 정기 모임 중복 생성 시 예외 발생 테스트
            assertThrows(IllegalArgumentException.class, () -> {
                matchingBoardService.createMatchingBoard(requestDTO);
            });
        });
    }

    @Test
    public void testCreateTemporaryMatchingBoard() {
        // 5개의 번개 모임 생성
        IntStream.rangeClosed(1, 5).forEach(i -> {
            MatchingBoardRequestDTO requestDTO = new MatchingBoardRequestDTO();
            requestDTO.setMemberId(testMembers.get(i - 1).getId()); // 회원 ID 설정
            requestDTO.setMatchingType(2); // 번개 모임
            requestDTO.setTitle("번개 모임 제목" + i);
            requestDTO.setContent("번개 모임 내용" + i);
            requestDTO.setPlaceName("장소" + i);
            requestDTO.setLocationX(BigDecimal.valueOf(37.5665 + i * 0.01));
            requestDTO.setLocationY(BigDecimal.valueOf(126.9780 + i * 0.01));
            requestDTO.setAddress("서울특별시" + i);
            requestDTO.setMeetDate(LocalDateTime.now()); // 당일 날짜로 설정
            requestDTO.setTag("태그" + i);
            requestDTO.setHeadCount(10 + i);

            // 번개 모임 생성 테스트
            MatchingBoard savedBoard = matchingBoardService.createMatchingBoard(requestDTO);
            assertThat(savedBoard).isNotNull();

            // 같은 날 번개 모임 중복 생성 시 예외 발생 테스트
            assertThrows(IllegalArgumentException.class, () -> {
                matchingBoardService.createMatchingBoard(requestDTO);
            });
        });
    }

    @Test
    public void testCreateSelfMatchingBoard() {
        // 5개의 셀프 소개팅 생성
        IntStream.rangeClosed(1, 5).forEach(i -> {
            MatchingBoardRequestDTO requestDTO = new MatchingBoardRequestDTO();
            requestDTO.setMemberId(testMembers.get(i - 1).getId()); // 회원 ID 설정
            requestDTO.setMatchingType(3); // 셀프 소개팅
            requestDTO.setTitle("셀프 소개팅 제목" + i);
            requestDTO.setContent("셀프 소개팅 내용" + i);
            requestDTO.setPlaceName("장소" + i);
            requestDTO.setLocationX(BigDecimal.valueOf(37.5665 + i * 0.01));
            requestDTO.setLocationY(BigDecimal.valueOf(126.9780 + i * 0.01));
            requestDTO.setAddress("서울특별시" + i);
            requestDTO.setMeetDate(LocalDateTime.now().plusDays(i));
            requestDTO.setTag("태그" + i);
            requestDTO.setHeadCount(10 + i);

            // 셀프 소개팅 생성 테스트
            MatchingBoard savedBoard = matchingBoardService.createMatchingBoard(requestDTO);
            assertThat(savedBoard).isNotNull();

            // 셀프 소개팅 게시물 중복 생성 시 예외 발생 테스트
            assertThrows(IllegalArgumentException.class, () -> {
                matchingBoardService.createMatchingBoard(requestDTO);
            });
        });
    }
}
