package com.luckyvicky.woosan.domain.board.service;

import com.cybozu.labs.langdetect.LangDetectException;
import com.luckyvicky.woosan.domain.board.dto.BoardDTO;

import java.io.IOException;

public interface SummaryService {
    String summaryBoardDetailPage(BoardDTO boardDTO) throws IOException, LangDetectException;
}