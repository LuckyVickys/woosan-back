package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
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
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberMatchingRepositoryTests {

    @Autowired
    private MemberMatchingRepository memberMatchingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @BeforeEach
    public void setup() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Member member = Member.builder()
                    .email("test" + i + "@example.com")
                    .nickname("nickname" + i)
                    .password("password")
                    .point(0L)
                    .memberType(MemberType.USER)
                    .level(MemberType.Level.LEVEL_1) // 레벨 설정 추가
                    .isActive(true)
                    .socialType(SocialType.NORMAL)
                    .build();
            memberRepository.save(member);

            MatchingBoard matchingBoard = MatchingBoard.builder()
                    .member(member)
                    .title("title" + i)
                    .content("content" + i)
                    .placeName("place" + i)
                    .locationX(BigDecimal.valueOf(0.0))
                    .locationY(BigDecimal.valueOf(0.0))
                    .address("address" + i)
                    .meetDate(LocalDateTime.now())
                    .tag("tag" + i)
                    .headCount(10)
                    .regDate(LocalDateTime.now())
                    .views(0)
                    .isDeleted(false)
                    .matchingType(3) // 셀프 소개팅 타입
                    .build();
            matchingBoardRepository.save(matchingBoard);
        });
    }

    @Test
    public void testAddMemberMatching() {
        memberRepository.findAll().forEach(member -> {
            MatchingBoard matchingBoard = matchingBoardRepository.findByMemberAndMatchingType(member, 3).get(0); // 셀프 소개팅 매칭 보드 가져오기
            MemberMatching memberMatching = MemberMatching.builder()
                    .matchingBoard(matchingBoard)
                    .member(member)
                    .isAccepted(false)
                    .isManaged(false)
                    .build();

            // Save the MemberMatching entity and log its ID
            MemberMatching savedMemberMatching = memberMatchingRepository.save(memberMatching);
            log.info("memberMatching_id: " + savedMemberMatching.getId());
        });
    }

    @Test
    public void testRead() {
        Long id = 1L;
        MemberMatching memberMatching = memberMatchingRepository.findById(id).orElse(null);
        log.info("--------------------");
        log.info(memberMatching);
    }
}
