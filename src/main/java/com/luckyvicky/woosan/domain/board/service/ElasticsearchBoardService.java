package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.SearchDTO;
import com.luckyvicky.woosan.domain.board.dto.SearchPageResponseDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;

import java.util.List;

public interface ElasticsearchBoardService {

    PageResponseDTO searchByCategoryAndFilter(PageRequestDTO pageRequestDTO, String categoryName, String filterType, String keyword);

    List<String> autocomplete(String category, String filterType, String keyword);

    List<String> getRealTimeSearchRankings();

    PageResponseDTO<SearchDTO> searchWithSynonyms(PageRequestDTO pageRequestDTO, String keyword);

    void saveSearchKeyword(String keyword);

    SearchPageResponseDTO searchWithStandardAndSynonyms(PageRequestDTO standardPageRequest, PageRequestDTO synonymPageRequest, String categoryName, String filterType, String keyword);
}
