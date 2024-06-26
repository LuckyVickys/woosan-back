package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardReplyRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MatchingBoardReplyRepositoryTests {

    @Autowired
    private MatchingBoardReplyRepository matchingBoardReplyRepository;

    @Test
    public void testAddMatchingBoardReply() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            MatchingBoardReply matchingBoardReply = new MatchingBoardReply();
            matchingBoardReply.setContent("댓글 내용 " + i);
            matchingBoardReply.setWriter("작성자 " + i);
            matchingBoardReply.setRegDate(LocalDateTime.now());
            matchingBoardReply.setParentId((long) i);
            matchingBoardReply.setMatchingId((long) i);

            // Save the MatchingBoardReply entity and log its ID
            MatchingBoardReply savedMatchingBoardReply = matchingBoardReplyRepository.save(matchingBoardReply);
            log.info("matchingBoardReply_id: " + savedMatchingBoardReply.getId());
        });
    }

    @Test
    public void testRead() {
        Long id = 1L;
        MatchingBoardReply matchingBoardReply = matchingBoardReplyRepository.findById(id).orElse(null);
        log.info("--------------------");
        log.info(matchingBoardReply);
    }
}
