package com.luckyvicky.woosan.domain.board.controller;

import com.luckyvicky.woosan.domain.board.dto.BoardPageResponseDTO;
import com.luckyvicky.woosan.domain.board.service.ElasticsearchBoardService;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
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
    public ResponseEntity<BoardPageResponseDTO> search(
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "filter", required = false, defaultValue = "titleOrContent") String filterType,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            PageRequestDTO pageRequestDTO) {

        BoardPageResponseDTO results = elasticsearchBoardService.searchByCategoryAndFilter(categoryName, filterType, keyword, pageRequestDTO);
        return ResponseEntity.ok(results);
    }

    /**
     * 검색 키워드 자동완성
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "searchType", required = false) String filterType,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<String> result = elasticsearchBoardService.autocomplete(categoryName, filterType, keyword);
        return ResponseEntity.ok(result);
    }
}

