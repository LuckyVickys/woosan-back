package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.SearchDTO;
import com.luckyvicky.woosan.domain.board.dto.SearchPageResponseDTO;
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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
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
    public PageResponseDTO searchByCategoryAndFilter(PageRequestDTO pageRequestDTO, String categoryName, String filterType, String keyword) {
        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = "전체";
        }

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").ascending());


        Page<Board> results;
        if (categoryName.equals("전체")) {
            switch (filterType) {
                case "title":
                    results = elasticsearchBoardRepository.findByTitleContainingAndCategoryNameNot(keyword, "공지사항", pageable);
                    break;
                case "content":
                    results = elasticsearchBoardRepository.findByContentContainingAndCategoryNameNot(keyword, "공지사항", pageable);
                    break;
                case "writer":
                    results = elasticsearchBoardRepository.findByNicknameContainingAndCategoryNameNot(keyword, "공지사항", pageable);
                    break;
                case "titleOrContent":
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryNameNot(keyword, keyword, "공지사항", pageable);
                    break;
                case "titleOrWriter":
                    results = elasticsearchBoardRepository.findByTitleContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, "공지사항", pageable);
                    break;
                case "contentOrWriter":
                    results = elasticsearchBoardRepository.findByContentContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, "공지사항", pageable);
                    break;
                case "titleOrContentOrWriter":
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, keyword, "공지사항", pageable);
                    break;
                default:
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryNameNot(keyword, keyword, "공지사항", pageable);
                    break;
            }
        } else {
            switch (filterType) {
                case "title":
                    results = elasticsearchBoardRepository.findByTitleContainingAndCategoryName(keyword, categoryName, pageable);
                    break;
                case "content":
                    results = elasticsearchBoardRepository.findByContentContainingAndCategoryName(keyword, categoryName, pageable);
                    break;
                case "writer":
                    results = elasticsearchBoardRepository.findByNicknameContainingAndCategoryName(keyword, categoryName, pageable);
                    break;
                case "titleOrContent":
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryName(keyword, keyword, categoryName, pageable);
                    break;
                case "titleOrWriter":
                    results = elasticsearchBoardRepository.findByTitleContainingOrNicknameContainingAndCategoryName(keyword, keyword, categoryName, pageable);
                    break;
                case "contentOrWriter":
                    results = elasticsearchBoardRepository.findByContentContainingOrNicknameContainingAndCategoryName(keyword, keyword, categoryName, pageable);
                    break;
                case "titleOrContentOrWriter":
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryName(keyword, keyword, keyword, categoryName, pageable);
                    break;
                default:
                    results = elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryName(keyword, keyword, categoryName, pageable);
                    break;
            }
        }

        List<BoardDTO> dtoList = results.stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());


        long totalCount = results.getTotalElements();
        PageResponseDTO<BoardDTO> boardPage = PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();

        return boardPage;
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
    public PageResponseDTO<SearchDTO> searchWithSynonyms(PageRequestDTO pageRequestDTO, String keyword) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").ascending());

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "synonym_title", "synonym_content")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .analyzer("synonym_ngram_analyzer"))
                .withPageable(pageable)
                .build();

        SearchHits<Board> searchHits = elasticsearchRestTemplate.search(searchQuery, Board.class);

        List<SearchDTO> synonymDtoList = searchHits.getSearchHits().stream()
                .map(hit -> modelMapper.map(hit.getContent(), SearchDTO.class))
                .collect(Collectors.toList());

        long totalCount = searchHits.getTotalHits();

        return PageResponseDTO.<SearchDTO>withAll()
                .dtoList(synonymDtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
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
                .withQuery(QueryBuilders.rangeQuery("timestamp").gte("now-30m"))
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


    @Override
    public SearchPageResponseDTO searchWithStandardAndSynonyms(PageRequestDTO standardPageRequest, PageRequestDTO synonymPageRequest, String categoryName, String filterType, String keyword) {
        PageResponseDTO<SearchDTO> standardResult = searchByCategoryAndFilter(standardPageRequest, categoryName, filterType, keyword);
        PageResponseDTO<SearchDTO> synonymResult = searchWithSynonyms(synonymPageRequest, keyword);

        return SearchPageResponseDTO.builder()
                .StandardResult(standardResult)
                .SynonymResult(synonymResult)
                .build();
    }

}
