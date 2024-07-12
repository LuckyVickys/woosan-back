package com.luckyvicky.woosan.domain.board.controller;

import com.luckyvicky.woosan.domain.board.dto.SearchPageResponseDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.service.ElasticsearchBoardService;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Log4j2
public class ElasticsearchBoardController {

    private final ElasticsearchBoardService elasticsearchBoardService;

    /**
     * 일반 검색
     */
//    @GetMapping("/search")
//    public ResponseEntity<PageResponseDTO> search(
//            @RequestParam(value = "category", required = false) String categoryName,
//            @RequestParam(value = "filter", required = false, defaultValue = "titleOrContent") String filterType,
//            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
//            PageRequestDTO pageRequestDTO) {
//
//        // 검색 키워드 저장
//        elasticsearchBoardService.saveSearchKeyword(keyword);
//
//        PageResponseDTO results = elasticsearchBoardService.searchByCategoryAndFilter(pageRequestDTO, categoryName, filterType, keyword);
//        return ResponseEntity.ok(results);
//    }


    /**
     *  검색 (기본 + 유의)
     */
    @GetMapping("/search")
    public ResponseEntity<SearchPageResponseDTO> search(
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "filter", required = false) String filterType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "standardPage", required = false, defaultValue = "1") int standardPage,
            @RequestParam(value = "synonymPage", required = false, defaultValue = "1") int synonymPage,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        PageRequestDTO standardPageRequest = new PageRequestDTO(standardPage, size);
        PageRequestDTO synonymPageRequest = new PageRequestDTO(synonymPage, size);

//         검색 키워드 저장
        elasticsearchBoardService.saveSearchKeyword(keyword);

        SearchPageResponseDTO result = elasticsearchBoardService.searchWithStandardAndSynonyms(standardPageRequest, synonymPageRequest, categoryName, filterType, keyword);

        return ResponseEntity.ok(result);
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


    /**
     * 동의/유의어 검색
     */
//    @GetMapping("/search/synonyms")
//    public ResponseEntity<List<Board>> searchWithSynonyms(@RequestParam String keyword) {
//        List<Board> results = elasticsearchBoardService.searchWithSynonyms(keyword);
//        log.info(results+"^^^^^^^^^^^^^");
//        return ResponseEntity.ok(results);
//    }

    /**
     * 검색 키워드 1시간 집계
     */
    @GetMapping("/ranking")
    public List<String> getRealTimeSearchRankings() {
        return elasticsearchBoardService.getRealTimeSearchRankings();
    }
}

