package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;

import java.util.List;

public interface ElasticsearchBoardService {

    List<Board> searchByCategoryAndFilter(String categoryName, String filterType, String keyword);

    List<String> autocomplete(String keyword, String searchType);

}
