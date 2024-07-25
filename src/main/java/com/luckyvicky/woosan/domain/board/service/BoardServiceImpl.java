package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.exception.BoardException;
import com.luckyvicky.woosan.domain.board.projection.IBoardList;
import com.luckyvicky.woosan.domain.board.projection.IUpdateBoard;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.global.util.ValidationHelper;
import com.luckyvicky.woosan.domain.board.projection.IBoardMember;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.util.CommonUtils;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.luckyvicky.woosan.global.util.Constants.*;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final FileImgService fileImgService;
    private final ElasticsearchBoardService elasticsearchBoardService;
    private final CommonUtils commonUtils;
    private final ValidationHelper validationHelper;


    /**
     * 게시물 작성
     */
    @Override
    public void createBoard(BoardDTO boardDTO, List<MultipartFile> images) {
        validationHelper.boardInput(boardDTO); // 입력값 검증
        Member writer = validationHelper.findWriterAndAddPoints(boardDTO.getWriterId(), 10); // 작성자 검증 및 조회
        memberRepository.save(writer);
        Board board = saveBoard(boardDTO, writer);
        handleFileUpload(images, board.getId()); //파일이 있으면 파일 정보를 버킷 및 db에 저장
    }


    /**
     * 게시물 다건 조회(공지사항1 + 인기글3 + 전체 조회)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardPageResponseDTO getBoardList(PageRequestDTO pageRequestDTO, String categoryName) {
        pageRequestDTO.validate();
        Pageable pageable = commonUtils.createPageable(pageRequestDTO);


        BoardListDTO notice = getNotice(NOTICE); // 공지사항 조회
        List<BoardListDTO> popularList = getTop3ByLikes(); // 인기글 조회

        // 카테고리에 따른 게시물 페이지 결과 조회 및 DTO 변환
        Page<IBoardList> result = getBoardsByCategoryPage(categoryName, pageable); // 전체 조회
        List<BoardListDTO> dtoList = commonUtils.mapToDTOList(result.getContent(), BoardListDTO.class); // DTO 리스트 변환

        PageResponseDTO<BoardListDTO> boardPage = commonUtils.createPageResponseDTO(pageRequestDTO, dtoList, result.getTotalElements()); // 페이지 응답 DTO 생성

        return BoardPageResponseDTO.builder()
                .notice(notice)
                .popularList(popularList)
                .boardPage(boardPage)
                .build();
    }


    /**
     * 공지사항 다건 조회 (cs page)
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardListDTO> getNoticePage(PageRequestDTO pageRequestDTO) {
        Pageable pageable = commonUtils.createPageable(pageRequestDTO);

        Page<IBoardList> result = boardRepository.findAllProjectedByCategoryNameAndIsDeletedFalseOrderByIdDesc(NOTICE, pageable);

        List<BoardListDTO> dtoList = commonUtils.mapToDTOList(result.getContent(), BoardListDTO.class);

        return commonUtils.createPageResponseDTO(pageRequestDTO, dtoList, result.getTotalElements());
    }


    /**
     * 게시물 단건 조회 - 상세 페이지
     */
    @Override
    @Transactional
    public BoardDetailDTO getBoard(Long id) {
        increaseViewCount(id); // 조회수 증가
        Board board = validationHelper.findBoard(id); // 게시물 조회
        IBoardMember boardMember = boardRepository.findById(id, IBoardMember.class)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
        BoardDTO boardDTO = commonUtils.mapObject(boardMember, BoardDTO.class);
        boardDTO.setViews(board.getViews());  // 최신 조회수 DTO에 반영
        boardDTO.setFilePathUrl(fileImgService.findFiles("board", id));   // 버킷에서 이미지 url 꺼내고 DTO에 반영
        boardDTO.setWriterProfile(fileImgService.findFiles("member", boardDTO.getWriterId()));   // 버킷에서 이미지 url 꺼내고 DTO에 반영


        // 연관 게시물 검색
        List<SuggestedBoardDTO> suggestedBoards = elasticsearchBoardService.getSuggestedBoards(board.getTitle(), board.getContent());

        return BoardDetailDTO.builder()
                .boardDTO(boardDTO)
                .suggestedBoards(suggestedBoards)
                .build();
    }


    /**
     * 공지사항 단건 조회 - 상세 페이지
     */
    @Override
    @Transactional
    public BoardDTO getNotice(Long id) {
        increaseViewCount(id); // 조회수 증가
        Board board = validationHelper.findBoard(id); // 게시물 조회
        IBoardMember boardMember = boardRepository.findByIdAndCategoryName(id, NOTICE)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
        BoardDTO boardDTO = commonUtils.mapObject(boardMember, BoardDTO.class);
        boardDTO.setViews(board.getViews());  // 최신 조회수 DTO에 반영
        boardDTO.setFilePathUrl(fileImgService.findFiles("board", id));   // 버킷에서 이미지 url 꺼내고 DTO에 반영
        boardDTO.setWriterProfile(fileImgService.findFiles("member", boardDTO.getWriterId()));   // 버킷에서 이미지 url 꺼내고 DTO에 반영

        return boardDTO;
    }

    /**
     * 게시물 단건 조회 (PATCH)
     */
    @Override
    public UpdateBoardDTO getBoardForUpdate(Long id) {
        validationHelper.boardExist(id); // 게시물 존재 여부 검증
        IUpdateBoard updateBoard = boardRepository.findById(id, IUpdateBoard.class)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
        UpdateBoardDTO updateBoardDTO = commonUtils.mapObject(updateBoard, UpdateBoardDTO.class);
        updateBoardDTO.setFilePathUrl(fileImgService.findFiles("board", id));
        return updateBoardDTO;
    }


    /**
     * 게시물 수정
     */
    @Override
    public void updateBoard(BoardDTO boardDTO, List<MultipartFile> images) {
        validationHelper.boardInput(boardDTO); // 입력값 검증
        validationHelper.checkBoardOwnership(boardDTO.getId(), boardDTO.getWriterId()); // 소유자 검증
        updateBoardContent(boardDTO, images);
    }


    /**
     * 게시물 삭제
     */
    @Override
    public void deleteBoard(RemoveDTO removeDTO) {
        validationHelper.boardExist(removeDTO.getId()); // 게시물 존재 여부 검증
        validationHelper.checkBoardOwnership(removeDTO.getId(), removeDTO.getWriterId()); // 소유자 검증
        markBoardAsDeleted(removeDTO.getId());
    }


    /**
     * 공지사항 게시물 10개 조회
     */
    @Override
    @Transactional
    public List<BoardListDTO> getNotices() {
        List<IBoardList> result = boardRepository.findTop5ProjectedByCategoryNameAndIsDeletedFalse(NOTICE);
        return commonUtils.mapToDTOList(result, BoardListDTO.class);
    }

    /**
     * 인기글 게시물 10개 조회
     */
    @Override
    @Transactional
    public List<BoardListDTO> getBestBoard() {
        List<IBoardList> result = boardRepository.findTop10ProjectedByIsDeletedFalseOrderByViewsDesc();
        return commonUtils.mapToDTOList(result, BoardListDTO.class);
    }





    /**
     * 게시물 저장
     */
    private Board saveBoard(BoardDTO boardDTO, Member writer) {
        Board board = Board.builder()
                .writer(writer)
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .categoryName(boardDTO.getCategoryName())
                .build();
        return boardRepository.save(board);
    }

    /**
     * 파일 업로드 처리
     */
    private void handleFileUpload(List<MultipartFile> images, Long boardId) {
        if(images != null){
            fileImgService.fileUploadMultiple("board", boardId, images);
        }
    }


    private Page<IBoardList> getBoardsByCategoryPage(String categoryName, Pageable pageable) {
        if (categoryName != null && !categoryName.isEmpty()) {
            return boardRepository.findAllProjectedByCategoryNameAndIsDeletedFalseOrderByIdDesc(categoryName, pageable);
        } else {
            return boardRepository.findAllProjectedByCategoryNameNotAndIsDeletedFalseOrderByIdDesc(NOTICE, pageable);
        }
    }


    /**
     * 최신 공지사항 단건 조회
     */
    @Transactional(readOnly = true)
    public BoardListDTO getNotice(String categoryName) {
        Optional<IBoardList> result = boardRepository.findFirstByCategoryNameAndIsDeletedFalse(categoryName);
        return result.map(boardList -> modelMapper.map(boardList, BoardListDTO.class)).orElse(null);
    }


    /**
     * 인기 게시물 상위 3개 조회
     */
    @Transactional(readOnly = true)
    public List<BoardListDTO> getTop3ByLikes() {
        List<IBoardList> result = boardRepository.findTop3ByIsDeletedFalseOrderByViewsDesc();
        return commonUtils.mapToDTOList(result, BoardListDTO.class);
    }


    /**
     * 게시물 조회수 증가
     */
    private void increaseViewCount(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
        board.addViewCount();
        boardRepository.save(board);
    }


    /**
     * 게시물 수정 처리
     */
    private void updateBoardContent(BoardDTO boardDTO, List<MultipartFile> images) {
        Board board = validationHelper.findBoard(boardDTO.getId());
        board.changeTitle(boardDTO.getTitle());
        board.changeContent(boardDTO.getContent());

        updateBoardFiles(boardDTO, images, board.getId());
        boardRepository.save(board);
    }


    /**
     * 게시물 파일 정보 갱신
     */
    private void updateBoardFiles(BoardDTO boardDTO, List<MultipartFile> images, Long boardId) {
        if (boardDTO.getFilePathUrl() == null) {
            fileImgService.targetFilesDelete("board", boardId);
        } else {
            List<String> beforeFiles = fileImgService.findFiles("board", boardId);
            List<String> afterFiles = boardDTO.getFilePathUrl();

            for (String beforeFile : beforeFiles) {
                if (!afterFiles.contains(beforeFile)) {
                    fileImgService.deleteS3FileByUrl(boardId, "board", beforeFile);
                }
            }
        }

        if (images != null) {
            fileImgService.fileUploadMultiple("board", boardId, images);
        }
    }


    /**
     * 게시물 삭제 처리
     */
    private void markBoardAsDeleted(Long boardId) {
        Board board = validationHelper.findBoard(boardId);
        board.changeIsDeleted(true);
        boardRepository.save(board);
    }

}


