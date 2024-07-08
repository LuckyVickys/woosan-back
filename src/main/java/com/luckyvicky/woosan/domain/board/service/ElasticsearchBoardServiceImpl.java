package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.repository.elasticsearch.ElasticsearchBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchBoardServiceImpl implements ElasticsearchBoardService {

    private final ElasticsearchBoardRepository elasticsearchBoardRepository;

    @Override
    public List<Board> searchByTitle(String keyword) {
        return elasticsearchBoardRepository.findByTitleContaining(keyword);
    }

    @Override
    public List<Board> searchByContent(String keyword) {
        return elasticsearchBoardRepository.findByContentContaining(keyword);
    }

//    @Override
//    public List<Board> searchByWriterName(String keyword) {
//        return elasticsearchBoardRepository.findByWriterNameContaining(keyword);
//    }

    @Override
    public List<Board> searchByTitleOrContent(String title, String content) {
        return elasticsearchBoardRepository.findByTitleContainingOrContentContaining(title, content);
    }

    @Override
    public List<Board> searchByTitleAndContent(String title, String content) {
        return elasticsearchBoardRepository.findByTitleContainingAndContentContaining(title, content);
    }

//    @Override
//    public List<Board> searchByTitleAndWriterName(String title, String writerName) {
//        return elasticsearchBoardRepository.findByTitleContainingAndWriterNameContaining(title, writerName);
//    }
//
//    @Override
//    public List<Board> searchByTitleContentAndWriterName(String title, String content, String writerName) {
//        return elasticsearchBoardRepository.findByTitleContainingAndContentContainingAndWriterNameContaining(title, content, writerName);
//    }
}
