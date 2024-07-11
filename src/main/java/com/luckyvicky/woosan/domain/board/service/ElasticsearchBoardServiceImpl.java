package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.BoardPageResponseDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.SearchKeyword;
import com.luckyvicky.woosan.domain.board.repository.elasticsearch.ElasticsearchBoardRepository;
import com.luckyvicky.woosan.domain.board.repository.elasticsearch.SearchKeywordRepository;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.modelmapper.ModelMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ElasticsearchBoardServiceImpl implements ElasticsearchBoardService {

    private final ElasticsearchBoardRepository elasticsearchBoardRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final SearchKeywordRepository searchKeywordRepository;
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


        if (categoryName.equals("전체")) {
            switch (filterType) {
                case "title":
                    result = elasticsearchBoardRepository.findByTitleOrKoreanTitleContainingAndCategoryNameNot(keyword);
                    return result.stream()
                            .map(Board::getTitle)
                            .distinct()
                            .collect(Collectors.toList());
                case "content":
                    result = elasticsearchBoardRepository.findByContentOrKoreanContentContainingAndCategoryNameNot(keyword);
                    return result.stream()
                            .map(Board::getContent)
                            .distinct()
                            .collect(Collectors.toList());
                case "writer":
                    result = elasticsearchBoardRepository.autocompleteWriter(keyword);
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
                    result = elasticsearchBoardRepository.findByTitleContainingOrKoreanTitleContainingAndCategoryNameEquals(keyword, keyword, categoryName);
                    return result.stream()
                            .map(Board::getTitle)
                            .distinct()
                            .collect(Collectors.toList());
                case "content":
                    result = elasticsearchBoardRepository.findByContentContainingOrKoreanContentContainingAndCategoryNameEquals(keyword, keyword, categoryName);
                    return result.stream()
                            .map(Board::getContent)
                            .distinct()
                            .collect(Collectors.toList());
                case "writer":
                    result = elasticsearchBoardRepository.autocompleteWriterAndCategoryName(keyword, categoryName);
                    return result.stream()
                            .map(Board::getNickname)
                            .distinct()
                            .collect(Collectors.toList());
                default:
                    return List.of(); // 빈 리스트 반환
            }
        }
    }


    /**
     * 동의/유의어 검색
     */
    @Override
    public List<Board> searchWithSynonyms(String keyword) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "synonym_title", "synonym_content")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .analyzer("synonym_ngram_analyzer"))
                .build();
        SearchHits<Board> searchHits = elasticsearchRestTemplate.search(searchQuery, Board.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    /**
     * 검색 키워드 저장
     */
    @Override
    public void saveSearchKeyword(String keyword) {
        SearchKeyword searchKeyword = new SearchKeyword();
        searchKeyword.setKeyword(keyword);

        // 현재 시간을 명시적으로 설정
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        searchKeyword.setTimestamp(now);

        log.info("Saving keyword: {}", searchKeyword);
        searchKeywordRepository.save(searchKeyword);
    }

    /**
     * 검색 키워드를 1시간 동안 집계
     */
    @Override
    public List<String> getRealTimeSearchRankings() {
        // Elasticsearch 쿼리 생성
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("timestamp").gte("now-1h"))
                .addAggregation(AggregationBuilders.terms("popular_keywords").field("keyword").size(10).order(BucketOrder.count(false)))
                .build();

        // 검색 실행 및 집계 결과 추출
        SearchHits<SearchKeyword> searchHits = elasticsearchRestTemplate.search(searchQuery, SearchKeyword.class, IndexCoordinates.of("search_keywords"));

        // 집계 데이터 추출
        Aggregations aggregations = searchHits.getAggregations();
        Terms popularKeywords = aggregations.get("popular_keywords");

        List<String> rankings = new ArrayList<>();
        for (Terms.Bucket bucket : popularKeywords.getBuckets()) {
            rankings.add(bucket.getKeyAsString());
        }

        return rankings;
    }


}
