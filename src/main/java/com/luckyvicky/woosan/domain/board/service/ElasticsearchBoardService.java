package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardPageResponseDTO;
import com.luckyvicky.woosan.global.util.PageRequestDTO;

import java.util.List;

public interface ElasticsearchBoardService {

    List<String> autocomplete(String category,String filterType, String keyword);
    BoardPageResponseDTO searchByCategoryAndFilter(String categoryName, String filterType, String keyword, PageRequestDTO pageRequestDTO);
}
