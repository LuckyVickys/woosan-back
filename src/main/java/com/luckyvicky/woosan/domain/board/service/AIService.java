package com.luckyvicky.woosan.domain.board.service;

import com.cybozu.labs.langdetect.LangDetectException;
import com.luckyvicky.woosan.domain.board.dto.BoardApiDTO;

import java.io.IOException;

public interface AIService {
    BoardApiDTO translateBoardDetailPage(BoardApiDTO boardApiDTO) throws IOException;
    String summaryBoardDetailPage(BoardApiDTO boardApiDTO) throws IOException, LangDetectException;
}
