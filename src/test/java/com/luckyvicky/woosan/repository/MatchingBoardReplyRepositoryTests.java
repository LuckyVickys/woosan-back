package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardReplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class MatchingBoardReplyRepositoryTests {

    @Autowired
    private MatchingBoardReplyRepository matchingBoardReplyRepository;

    @BeforeEach
    public void setUp() {
        for (int i = 1; i <= 10; i++) {
            MatchingBoardReply reply = MatchingBoardReply.builder()
                    .content("This is reply content " + i)
                    .writer("Writer " + i)
                    .regDate(LocalDateTime.now())
                    .parentId(null)
                    .matchingId((long) i) // Assuming matchingId from 1 to 10 exists
                    .build();

            matchingBoardReplyRepository.save(reply);
        }
    }

    @Test
    public void testInsertDummyData() {
        long count = matchingBoardReplyRepository.count();
        assertThat(count).isEqualTo(10);
    }
}
