package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;

import java.util.List;

public interface ElasticsearchBoardService {

    PageResponseDTO searchByCategoryAndFilter(PageRequestDTO pageRequestDTO, String categoryName, String filterType, String keyword);

    List<String> autocomplete(String category, String filterType, String keyword);

    PageResponseDTO<SearchDTO> searchWithSynonyms(PageRequestDTO pageRequestDTO, String keyword);

    void saveSearchKeyword(String keyword);

    List<RankingDTO> getRankingChanges();

    SearchPageResponseDTO searchWithStandardAndSynonyms(PageRequestDTO standardPageRequest, PageRequestDTO synonymPageRequest, String categoryName, String filterType, String keyword);

    List<DailyBestBoardDTO> getTop5BoardsByViews();

    List<SuggestedBoardDTO> getSuggestedBoards(Long id, String title, String content);
}
