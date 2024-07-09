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


    @GetMapping("/search")
    public ResponseEntity<List<Board>> search(
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "filter", required = false, defaultValue = "titleOrContent") String filterType,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        List<Board> results = elasticsearchBoardService.searchByCategoryAndFilter(categoryName, filterType, keyword);
        return ResponseEntity.ok(results);
    }
}
