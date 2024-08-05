package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class MemberMatchingRepositoryTests {

    @Autowired
    private MemberMatchingRepository memberMatchingRepository;

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        for (int i = 1; i <= 10; i++) {
            Member member = memberRepository.findById((long) i).orElseThrow();
            MatchingBoard matchingBoard = matchingBoardRepository.findById((long) i).orElseThrow();

            MemberMatching memberMatching = MemberMatching.builder()
                    .matchingBoard(matchingBoard)
                    .member(member)
                    .isAccepted(true)
                    .isManaged(false)
                    .build();

            memberMatchingRepository.save(memberMatching);
        }
    }

    @Test
    public void testInsertDummyData() {
        long count = memberMatchingRepository.count();
        assertThat(count).isEqualTo(10);
    }
}
