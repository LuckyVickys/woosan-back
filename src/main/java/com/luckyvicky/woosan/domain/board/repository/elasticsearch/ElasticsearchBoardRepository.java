package com.luckyvicky.woosan.domain.board.repository.elasticsearch;

import com.luckyvicky.woosan.domain.board.entity.Board;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticsearchBoardRepository extends ElasticsearchRepository<Board, Long> {

    List<Board> findByTitleContaining(String title);

    List<Board> findByContentContaining(String content);

//    List<Board> findByWriterNameContaining(String writerName);

    List<Board> findByTitleContainingOrContentContaining(String title, String content);

    List<Board> findByTitleContainingAndContentContaining(String title, String content);

//    List<Board> findByTitleContainingAndWriterNameContaining(String title, String writerName);

//    List<Board> findByTitleContainingAndContentContainingAndWriterNameContaining(String title, String content, String writerName);
}
