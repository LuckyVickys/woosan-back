package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.BoardPageResponseDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.repository.elasticsearch.ElasticsearchBoardRepository;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ElasticsearchBoardServiceImpl implements ElasticsearchBoardService {

    private final ElasticsearchBoardRepository elasticsearchBoardRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final ModelMapper modelMapper;

    /**
     * 기본 검색
     */
    @Override
    public BoardPageResponseDTO searchByCategoryAndFilter(String categoryName, String filterType, String keyword, PageRequestDTO pageRequestDTO) {
        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = "전체";
        }

        List<Board> results;
        if (categoryName.equals("전체")) {
            switch (filterType) {
                case "title":
                    results = elasticsearchBoardRepository.findByTitleContainingAndCategoryNameNot(keyword, "공지사항");
                    break;
                case "content":
                    results = elasticsearchBoardRepository.findByContentContainingAndCategoryNameNot(keyword, "공지사항");
                    break;
                case "writer":
                    results = elasticsearchBoardRepository.findByNicknameContainingAndCategoryNameNot(keyword, "공지사항");
                    break;
                case "titleOrContent":
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryNameNot(keyword, keyword, "공지사항");
                    break;
                case "titleOrWriter":
                    results = elasticsearchBoardRepository.findByTitleContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, "공지사항");
                    break;
                case "contentOrWriter":
                    results = elasticsearchBoardRepository.findByContentContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, "공지사항");
                    break;
                case "titleOrContentOrWriter":
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, keyword, "공지사항");
                    break;
                default:
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryNameNot(keyword, keyword, "공지사항");
                    break;
            }
        } else {
            switch (filterType) {
                case "title":
                    results = elasticsearchBoardRepository.findByTitleContainingAndCategoryName(keyword, categoryName);
                    break;
                case "content":
                    results = elasticsearchBoardRepository.findByContentContainingAndCategoryName(keyword, categoryName);
                    break;
                case "writer":
                    results = elasticsearchBoardRepository.findByNicknameContainingAndCategoryName(keyword, categoryName);
                    break;
                case "titleOrContent":
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryName(keyword, keyword, categoryName);
                    break;
                case "titleOrWriter":
                    results = elasticsearchBoardRepository.findByTitleContainingOrNicknameContainingAndCategoryName(keyword, keyword, categoryName);
                    break;
                case "contentOrWriter":
                    results = elasticsearchBoardRepository.findByContentContainingOrNicknameContainingAndCategoryName(keyword, keyword, categoryName);
                    break;
                case "titleOrContentOrWriter":
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryName(keyword, keyword, keyword, categoryName);
                    break;
                default:
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryName(keyword, keyword, categoryName);
                    break;
            }
        }

        List<BoardDTO> dtoList = results.stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());

        long totalCount = results.size();
        PageResponseDTO<BoardDTO> boardPage = PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();

        return BoardPageResponseDTO.builder()
                .boardPage(boardPage)
                .build();
    }


    @Override
    public List<String> autocomplete(String categoryName, String filterType, String keyword) {
        List<Board> result;

        System.out.println("=============================================================================");
        System.out.println("Category: " + categoryName);
        System.out.println("FilterType: " + filterType);
        System.out.println("Keyword: " + keyword);
        System.out.println("=============================================================================");

        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = "전체";
        }

        if (categoryName.equals("전체")) {
            switch (filterType) {
                case "title":
                    System.out.println("Searching for titles containing keyword (전체)");
                    result = elasticsearchBoardRepository.findByTitleContaining(keyword);
                    System.out.println("Results: " + result);
                    return result.stream()
                            .map(Board::getTitle)
                            .distinct()
                            .collect(Collectors.toList());
                case "content":
                    System.out.println("Searching for content containing keyword (전체)");
                    result = elasticsearchBoardRepository.findByContentContainingAndCategoryNameNot(keyword, "공지사항");
                    System.out.println("Results: " + result);
                    return result.stream()
                            .map(Board::getContent)
                            .distinct()
                            .collect(Collectors.toList());
                case "writer":
                    System.out.println("Searching for writers containing keyword (전체)");
                    result = elasticsearchBoardRepository.autocompleteWriter(keyword);
                    System.out.println("Results: " + result);
                    return result.stream()
                            .map(Board::getNickname)
                            .distinct()
                            .collect(Collectors.toList());
                default:
                    System.out.println("Invalid filter type (전체)");
                    return List.of(); // 빈 리스트 반환
            }
        } else {
            switch (filterType) {
                case "title":
                    System.out.println("Searching for titles containing keyword (카테고리: " + categoryName + ")");
                    result = elasticsearchBoardRepository.findByTitleContainingAndCategoryName(keyword, categoryName);
                    System.out.println("Results: " + result);
                    return result.stream()
                            .map(Board::getTitle)
                            .distinct()
                            .collect(Collectors.toList());
                case "content":
                    System.out.println("Searching for content containing keyword (카테고리: " + categoryName + ")");
                    result = elasticsearchBoardRepository.findByContentContainingAndCategoryName(keyword, categoryName);
                    System.out.println("Results: " + result);
                    return result.stream()
                            .map(Board::getContent)
                            .distinct()
                            .collect(Collectors.toList());
                case "writer":
                    System.out.println("Searching for writers containing keyword (카테고리: " + categoryName + ")");
                    result = elasticsearchBoardRepository.autocompleteWriterAndCategoryName(keyword, categoryName);
                    System.out.println("Results: " + result);
                    return result.stream()
                            .map(Board::getNickname)
                            .distinct()
                            .collect(Collectors.toList());
                default:
                    System.out.println("Invalid filter type (카테고리: " + categoryName + ")");
                    return List.of(); // 빈 리스트 반환
            }
        }
    }




    @Override
    public List<Board> searchWithSynonyms(String keyword) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content")
                        .analyzer("synonym_analyzer"))
                .build();
        SearchHits<Board> searchHits = elasticsearchRestTemplate.search(searchQuery, Board.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

}
