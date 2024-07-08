package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.entity.Board;

import java.util.List;

public interface ElasticsearchBoardService {
    List<Board> searchByTitle(String keyword);
    List<Board> searchByContent(String keyword);
//    List<Board> searchByWriterName(String keyword);
    List<Board> searchByTitleOrContent(String title, String content);
    List<Board> searchByTitleAndContent(String title, String content);
//    List<Board> searchByTitleAndWriterName(String title, String writerName);
//    List<Board> searchByTitleContentAndWriterName(String title, String content, String writerName);
}
