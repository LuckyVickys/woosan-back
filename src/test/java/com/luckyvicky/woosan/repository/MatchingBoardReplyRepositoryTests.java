package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardReplyRepository;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.entity.SocialType;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MatchingBoardReplyRepositoryTests {

    @Autowired
    private MatchingBoardReplyRepository matchingBoardReplyRepository;

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testAddMatchingBoardReply() {
        // Create 10 members with unique emails
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Member member = Member.builder()
                    .email("test" + i + "_" + System.currentTimeMillis() + "@example.com") // Ensure unique email
                    .nickname("nickname" + i)
                    .password("password")
                    .point(0L)
                    .memberType(MemberType.USER)
                    .level(MemberType.Level.values()[i % MemberType.Level.values().length]) // Assign level
                    .isActive(true)
                    .socialType(SocialType.NORMAL)
                    .build();
            memberRepository.save(member);
        });

        // Create 10 matching boards
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Member member = memberRepository.findById((long) i).orElseThrow();
            MatchingBoard matchingBoard = MatchingBoard.builder()
                    .member(member)
                    .manager(member) // Set manager
                    .matchingType(i % 3 + 1)
                    .title("모임 " + i)
                    .content("모임 설명 " + i)
                    .placeName("장소 " + i)
                    .locationX(BigDecimal.valueOf(37.5665 + i))
                    .locationY(BigDecimal.valueOf(126.9780 + i))
                    .address("주소 " + i)
                    .meetDate(LocalDateTime.now().plusDays(i))
                    .tag("태그 " + i)
                    .headCount(i)
                    .regDate(LocalDateTime.now())
                    .views(0)
                    .isDeleted(false)
                    .build();
            matchingBoardRepository.save(matchingBoard);
        });

        // Create 10 matching board replies
        IntStream.rangeClosed(1, 10).forEach(i -> {
            MatchingBoard matchingBoard = matchingBoardRepository.findById((long) i).orElseThrow();
            MatchingBoardReply reply = MatchingBoardReply.builder()
                    .matchingId(matchingBoard.getId())
                    .parentId(0L) // Set parentId to 0 or any valid value
                    .regDate(LocalDateTime.now())
                    .content("댓글 내용 " + i)
                    .writer("작성자 " + i)
                    .build();
            matchingBoardReplyRepository.save(reply);
        });

        log.info("Test for adding matching board replies completed.");
    }
}
