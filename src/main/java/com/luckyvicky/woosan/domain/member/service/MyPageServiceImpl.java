package com.luckyvicky.woosan.domain.member.service;


import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.member.dto.MyReplyDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;

import com.luckyvicky.woosan.domain.board.projection.IMyBoard;

import com.luckyvicky.woosan.domain.board.projection.IMyReply;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.jpa.ReplyRepository;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.domain.likes.repository.LikesRepository;
import com.luckyvicky.woosan.domain.member.dto.MyBoardDTO;
import com.luckyvicky.woosan.domain.member.dto.MyPageDTO;
import com.luckyvicky.woosan.domain.member.dto.MyReplyDTO;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
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
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MyPageServiceImpl implements MyPageService {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final LikesRepository likesRepository;
    private final BoardService boardService;
    private final ModelMapper modelMapper;


      @Transactional
    public PageResponseDTO<MyBoardDTO> getMyBoard(MyPageDTO myPageDTO) {
        Long memberId = myPageDTO.getMemberId();
        PageRequestDTO pageRequestDTO = myPageDTO.getPageRequestDTO();
        boardService.validateWriterId(memberId);

        pageRequestDTO.validate();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());

        Page<IMyBoard> myBoards = boardRepository.findByWriterId(memberId, pageable);

        List<MyBoardDTO> myBoardDTOs = myBoards.getContent().stream()
                .map(myBoard -> modelMapper.map(myBoard, MyBoardDTO.class))
                .collect(Collectors.toList());

        long totalCount = myBoards.getTotalElements();

        return PageResponseDTO.<MyBoardDTO>withAll()
                .dtoList(myBoardDTOs)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }



    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MyReplyDTO> getMyReply(MyPageDTO myPageDTO) {
        Long memberId = myPageDTO.getMemberId();
        PageRequestDTO pageRequestDTO = myPageDTO.getPageRequestDTO();
        boardService.validateWriterId(memberId);

        pageRequestDTO.validate();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());

        Page<IMyReply> myReplies = replyRepository.findByWriterId(memberId, pageable);

        List<MyReplyDTO> myReplyDTOs = myReplies.getContent().stream()
                .map(myReply -> modelMapper.map(myReply, MyReplyDTO.class))
                .collect(Collectors.toList());

        long totalCount = myReplies.getTotalElements();

        return PageResponseDTO.<MyReplyDTO>withAll()
                .dtoList(myReplyDTOs)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }

    @Override
    public PageResponseDTO<BoardDTO> myLikeBoardList(MyPageDTO myPageDTO) {
        myPageDTO.getPageRequestDTO().validate();
        Pageable pageable = PageRequest.of(myPageDTO.getPageRequestDTO().getPage() - 1, myPageDTO.getPageRequestDTO().getSize(), Sort.by("id").descending());
        Page<Board> result = boardRepository.findLikedBoards(myPageDTO.getMemberId(), pageable);

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());
        long totalCount = result.getTotalElements();

        return PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(myPageDTO.getPageRequestDTO())
                .totalCount(totalCount)
                .build();
    }

}
