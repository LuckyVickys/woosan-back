package com.luckyvicky.woosan.domain.admin.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.RemoveDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    Long createNotice(BoardDTO boardDTO, List<MultipartFile> images);

    void updateNoitce(BoardDTO boardDTO, List<MultipartFile> images);

    void deleteNotice(RemoveDTO removeDTO);
}
