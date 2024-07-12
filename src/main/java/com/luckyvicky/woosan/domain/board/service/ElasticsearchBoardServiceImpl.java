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
import java.util.Arrays;
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

    /**
     * 키워드 자동완성
     */

    private String generateShouldQuery(String[] keywords, String filterType) {
        StringBuilder shouldQuery = new StringBuilder();
        for (String keyword : keywords) {
            String sanitizedKeyword = keyword.replaceAll("\\s+", "");
            switch (filterType) {
                case "title":
                    shouldQuery.append("{\"match_phrase_prefix\": {\"title\": \"").append(sanitizedKeyword).append("\"}},");
                    shouldQuery.append("{\"match_phrase_prefix\": {\"korean_title\": \"").append(sanitizedKeyword).append("\"}},");
                    break;
                case "content":
                    shouldQuery.append("{\"match_phrase_prefix\": {\"content\": \"").append(sanitizedKeyword).append("\"}},");
                    shouldQuery.append("{\"match_phrase_prefix\": {\"korean_content\": \"").append(sanitizedKeyword).append("\"}},");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid filter type: " + filterType);
            }
        }
        return shouldQuery.substring(0, shouldQuery.length() - 1); // 마지막 쉼표 제거
    }

    @Override
    public List<String> autocomplete(String categoryName, String filterType, String keyword) {
        List<Board> result;
        String[] keywords = keyword.split("\\s+");
        String shouldQuery = generateShouldQuery(keywords, filterType);

        if (categoryName.equals("전체")) {
            switch (filterType) {
                case "title":
                    result = elasticsearchBoardRepository.findByTitleOrKoreanTitleContainingAndCategoryNameNot(shouldQuery);
                    return filterResults(result, keywords);
                case "content":
                    result = elasticsearchBoardRepository.findByContentOrKoreanContentContainingAndCategoryNameNot(shouldQuery);
                    return filterResults(result, keywords);
                case "writer":
                    result = elasticsearchBoardRepository.autocompleteWriter(shouldQuery);
                    return filterResults(result, keywords);
                default:
                    System.out.println("Invalid filter type (전체)");
                    return List.of(); // 빈 리스트 반환
            }
        } else {
            switch (filterType) {
                case "title":
                    result = elasticsearchBoardRepository.findByTitleContainingOrKoreanTitleContainingAndCategoryNameEquals(shouldQuery, categoryName);
                    return filterResults(result, keywords);
                case "content":
                    result = elasticsearchBoardRepository.findByContentContainingOrKoreanContentContainingAndCategoryNameEquals(shouldQuery, categoryName);
                    return filterResults(result, keywords);
                case "writer":
                    result = elasticsearchBoardRepository.autocompleteWriterAndCategoryName(shouldQuery, categoryName);
                    return filterResults(result, keywords);
                default:
                    return List.of(); // 빈 리스트 반환
            }
        }
    }

    private List<String> filterResults(List<Board> results, String[] keywords) {
        return results.stream()
                .filter(board -> containsAllKeywords(board, keywords))
                .map(board -> board.getTitle().trim())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean containsAllKeywords(Board board, String[] keywords) {
        String content = board.getTitle().replaceAll("\\s+", "") +
                board.getKoreanTitle().replaceAll("\\s+", "") +
                board.getContent().replaceAll("\\s+", "") +
                board.getKoreanContent().replaceAll("\\s+", "");
        return Arrays.stream(keywords)
                .map(keyword -> keyword.replaceAll("\\s+", ""))
                .allMatch(keyword -> containsKeyword(content, keyword));
    }

    private boolean containsKeyword(String content, String keyword) {
        // 정확히 일치하는 초성 검색을 위해 정규식을 사용
        String regex = "(?i).*" + keyword + ".*";
        return content.matches(regex);
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
