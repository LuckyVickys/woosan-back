package com.luckyvicky.woosan.domain.board.controller;

import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.service.ElasticsearchBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class ElasticsearchBoardController {

    private final ElasticsearchBoardService elasticsearchBoardService;

    @GetMapping("/search/title")
    public ResponseEntity<List<Board>> searchByTitle(@RequestParam("keyword") String keyword) {
        List<Board> results = elasticsearchBoardService.searchByTitle(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/content")
    public ResponseEntity<List<Board>> searchByContent(@RequestParam("keyword") String keyword) {
        List<Board> results = elasticsearchBoardService.searchByContent(keyword);
        return ResponseEntity.ok(results);
    }

//    @GetMapping("/search/writer")
//    public ResponseEntity<List<Board>> searchByWriterName(@RequestParam("keyword") String keyword) {
//        List<Board> results = elasticsearchBoardService.searchByWriterName(keyword);
//        return ResponseEntity.ok(results);
//    }

    @GetMapping("/search/title-or-content")
    public ResponseEntity<List<Board>> searchByTitleOrContent(@RequestParam("title") String title, @RequestParam("content") String content) {
        List<Board> results = elasticsearchBoardService.searchByTitleOrContent(title, content);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/title-and-content")
    public ResponseEntity<List<Board>> searchByTitleAndContent(@RequestParam("title") String title, @RequestParam("content") String content) {
        List<Board> results = elasticsearchBoardService.searchByTitleAndContent(title, content);
        return ResponseEntity.ok(results);
    }

//    @GetMapping("/search/title-and-writer")
//    public ResponseEntity<List<Board>> searchByTitleAndWriterName(@RequestParam("title") String title, @RequestParam("writer") String writerName) {
//        List<Board> results = elasticsearchBoardService.searchByTitleAndWriterName(title, writerName);
//        return ResponseEntity.ok(results);
//    }
//
//    @GetMapping("/search/title-content-writer")
//    public ResponseEntity<List<Board>> searchByTitleContentAndWriterName(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("writer") String writerName) {
//        List<Board> results = elasticsearchBoardService.searchByTitleContentAndWriterName(title, content, writerName);
//        return ResponseEntity.ok(results);
//    }
}
