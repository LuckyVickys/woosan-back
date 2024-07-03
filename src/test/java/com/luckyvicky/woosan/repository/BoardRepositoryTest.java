package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@SpringBootTest
public class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    MemberRepository memberRepository;



    @Test
    public void addTest() {

        Member testMember = memberRepository.findById(1L).orElseThrow();

        for (int i = 0; i < 100; i++) {
            Board board = Board.builder()
                    .writer(testMember)
                    .title("test" + i)
                    .content("배수지 " + i)
                    .regDate(LocalDateTime.now())
                    .views(0)
                    .isDeleted(false)
                    .categoryName("인테리어" + i)
                    .build();

            Board savedBoard = boardRepository.save(board);
            log.info("Board created: " + savedBoard);
        }
    }

    @Test
    @Transactional
    public void readTest() {
        Optional<Board> Board = boardRepository.findById(5L);
        log.info("Board: " + Board);
    }

    @Commit
    @Test
    @Transactional
    public void updateTest() {
        Member testMember = memberRepository.findById(1L).orElseThrow();
        Board board = boardRepository.findById(4L).orElseThrow();


        board.changeTitle("수정테스트");
        board.changeContent("수정 완료");


        boardRepository.save(board);
        log.info("Board updated: " + board);
    }


    @Commit
    @Transactional
    @Test
    public void deleteTest() {
        Member testMember = memberRepository.findById(1L).orElseThrow();
        Board board = boardRepository.findById(3L).orElseThrow();

        board.changeIsDeleted(true);
        log.info("Board deleted: " + board);
    }

    @Test
    @Transactional
    public void listAllTest() {
        List<Board> boards = boardRepository.findAll();
        boards.forEach(board -> log.info("Board: " + board));
    }

    @Test
    @Transactional
    public void testPaging(){
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id").ascending());
        Page<Board> result = boardRepository.findAll(pageable);

        log.info("Total Pages: " + result.getTotalPages());
        log.info("Total Elements: " + result.getTotalElements());
        log.info("Current Page Number: " + result.getNumber());
        log.info("Page Size: " + result.getSize());
        log.info("Has Next Page: " + result.hasNext());
        log.info("Has Previous Page: " + result.hasPrevious());

        result.getContent().forEach(board -> log.info("Board: " + board));
    }



    @Test
    public void testProjection(){
//        List<BoardProjection> boards = boardRepository.findAllProjectedBy(BoardProjection.class);


    }
}
