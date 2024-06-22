package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.dto.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.projection.IBoardMember;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;


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
        Optional<IBoardMember> result = boardRepository.findById(id, IBoardMember.class);

        IBoardMember boardMember = result.orElse(null);

        BoardDTO boardDTO = modelMapper.map(boardMember, BoardDTO.class);

        return boardDTO;
    }




    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardDTO> getlist(PageRequestDTO pageRequestDTO, String categoryName) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").ascending());

        Page<IBoardMember> result;

        if(categoryName != null && !categoryName.isEmpty()){
            result = boardRepository.findAllProjectedByCategoryName(categoryName, pageable, IBoardMember.class);
        } else {
            result = boardRepository.findAllProjectedBy(pageable, IBoardMember.class);
        }

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<BoardDTO> responseDTO = PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();

        return responseDTO;


    }







// <--------------------------------------------프로젝션-------------------------------------------->
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
