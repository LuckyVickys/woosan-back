package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.dto.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.projection.IBoardMember;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;


    @Override
    public Long register(BoardDTO boardDTO) {
        return 0L;
    }


    @Override
    public void modify(BoardDTO boardDTO) {

    }

    @Override
    public void delete(Long id) {

    }



    @Override
    public BoardDTO get(Long id) {
        return null;
    }




    @Override
    public PageResponseDTO<BoardDTO> getlist(PageRequestDTO pageRequestDTO) {
        return null;
    }


// <-----------------프로젝션----------------->
    /**
     *  <Test>
     * board 단일 인터페이스 프로젝션
     */
//    @Override
//    @Transactional(readOnly = true)
//    public List<IBoard> findAllProjectedBoard() {
//        return boardRepository.findAllProject(IBoard.class);
//    }



    /**
     * board member 연관관계 인터페이스 프로젝션
     * 게시물 단건 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<IBoardMember> findProjectedBoardMemberById(Long id) {
        return boardRepository.findById(id, IBoardMember.class);
    }


    /**
     * 연관관계 인터페이스 프로젝션
     * 게시물 전체 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IBoardMember> findAllProjectedBoardMember(Pageable pageable) {
        return boardRepository.findAllProjectedBy(pageable, IBoardMember.class);
    }


    /**
     * 카테고리별 게시물 전체 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IBoardMember> findAllProjectedBoardMemberByCategoryName(String categoryName, Pageable pageable) {
        return boardRepository.findAllProjectedByCategoryName(categoryName, pageable, IBoardMember.class);
    }

}
