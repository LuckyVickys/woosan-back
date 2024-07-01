package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class MatchingBoardRepositoryTests {

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        for (int i = 1; i <= 10; i++) {
            Member member = memberRepository.findById((long) i).orElseThrow();
            MatchingBoard matchingBoard = MatchingBoard.builder()
                    .member(member)
                    .matchingType(1)
                    .title("Title " + i)
                    .content("Content " + i)
                    .regDate(LocalDateTime.now())
                    .views(0)
                    .isDeleted(false)
                    .placeName("Place " + i)
                    .locationX(new BigDecimal("37.123456"))
                    .locationY(new BigDecimal("127.123456"))
                    .address("Address " + i)
                    .meetDate(LocalDateTime.now().plusDays(i))
                    .tag("Tag " + i)
                    .headCount(i)
                    .profile(null) // 필요시 프로필 설정
                    .build();
            matchingBoardRepository.save(matchingBoard);
        }
    }

    @Test
    public void testInsertDummyData() {
        long count = matchingBoardRepository.count();
        assertThat(count).isEqualTo(10);
    }
}
