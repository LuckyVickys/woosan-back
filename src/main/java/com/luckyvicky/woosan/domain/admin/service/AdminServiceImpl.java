package com.luckyvicky.woosan.domain.admin.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.RemoveDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.global.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final BoardRepository boardRepository;
    private final FileImgService fileImgService;
    private final ValidationHelper validationHelper;


    /**
     * 공지사항 작성
     */
    @Override
    public Long createNotice(BoardDTO boardDTO, List<MultipartFile> images) {
        validationHelper.noticeInput(boardDTO); // 입력값 검증
        Member writer = validationHelper.checkAndFindAdmin(boardDTO.getWriterId()); // 관리자 여부 검증
        Board board = saveBoard(boardDTO, writer);

        //파일이 있으면 파일 정보를 버킷 및 db에 저장합니다.
        if (images != null) {
            fileImgService.fileUploadMultiple("board", board.getId(), images);
        }

        return board.getId();
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
     * 게시물 수정
     */
    @Override
    public void updateNoitce(BoardDTO boardDTO, List<MultipartFile> images) {
        validationHelper.noticeInput(boardDTO); // 입력값 검증
        validationHelper.checkAdmin(boardDTO.getWriterId()); // 관리자 여부 검증

        Board board = validationHelper.findBoard(boardDTO.getId());
        board.changeTitle(boardDTO.getTitle());
        board.changeContent(boardDTO.getContent());

        if (boardDTO.getFilePathUrl() == null) {
            fileImgService.targetFilesDelete("board", board.getId());
        } else {
            List<String> beforeFiles = fileImgService.findFiles("board", board.getId());
            List<String> afterFiles = boardDTO.getFilePathUrl();

            for (String beforeFile : beforeFiles) {
                if (!afterFiles.contains(beforeFile)) {
                    fileImgService.deleteS3FileByUrl(board.getId(), "board", beforeFile);
                }
            }
        }

        if (images != null) {
            fileImgService.fileUploadMultiple("board", board.getId(), images);
        }
        boardRepository.save(board);
    }


    /**
     * 게시물 삭제
     */
    @Override
    public void deleteNotice(RemoveDTO removeDTO) {
        validationHelper.checkAndFindAdmin(removeDTO.getWriterId()); // 관리자 여부 검증

        Board board = validationHelper.findBoard(removeDTO.getId());
        validationHelper.checkBoardNotDeleted(board); // 게시물 삭제 여부 확인

        board.changeIsDeleted(true);
        boardRepository.save(board);
    }
}
