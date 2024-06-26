package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Log4j2
@SpringBootTest
public class MatchingBoardRepositoryTests {

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @Test
    public void testAddMatchingBoard(){
        IntStream.rangeClosed(1,5).forEach(i->{
            MatchingBoard matchingBoard = MatchingBoard.builder()
                    .postType(1)
                    .title("정기모임 제목" +i)
                    .content("정기모임 내용" +i)
                    .regDate(LocalDateTime.now())
                    .views(0)
                    .isDeleted(false)
                    .placeName("정기모임 장소" +i)
                    .locationX(BigDecimal.valueOf(37.5665 + i *0.01))
                    .locationY(BigDecimal.valueOf(126.9780 + i *0.01))
                    .address("서울특별시" + i)
                    .meetDate(LocalDateTime.now().plusDays(i))
                    .type("타입" + i)
                    .tag("태그" +i)
                    .headCount(10 + i)
                    .build();

                    log.info("matchingBoard_id: "+ matchingBoardRepository.save(matchingBoard).getId());
        });
    }

    @Test
    public void testRead(){
        Long id =1L;
        MatchingBoard matchingBoard = matchingBoardRepository.findById(id).orElse(null);
        log.info("--------------------");
        log.info(matchingBoard);
    }
}
