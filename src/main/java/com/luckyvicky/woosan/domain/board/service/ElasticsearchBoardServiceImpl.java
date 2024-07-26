package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.SearchKeyword;
import com.luckyvicky.woosan.domain.board.repository.elasticsearch.ElasticsearchBoardRepository;
import com.luckyvicky.woosan.domain.board.repository.elasticsearch.SearchKeywordRepository;
import com.luckyvicky.woosan.global.util.CommonUtils;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.luckyvicky.woosan.global.util.Constants.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class ElasticsearchBoardServiceImpl implements ElasticsearchBoardService {

    private final ElasticsearchBoardRepository elasticsearchBoardRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final SearchKeywordRepository searchKeywordRepository;
    private final CommonUtils commonUtils;

    /**
     * 기본 검색
     */
    @Override
    public PageResponseDTO<SearchDTO> searchByCategoryAndFilter(PageRequestDTO pageRequestDTO, String categoryName, String filterType, String keyword) {
        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = CATEGORY_ALL;
        }

        Pageable pageable = commonUtils.createPageable(pageRequestDTO);

        Page<Board> results = getSearchResults(categoryName, filterType, keyword, pageable);

        List<SearchDTO> dtoList = commonUtils.mapToDTOList(results.getContent(), SearchDTO.class);

        return commonUtils.createPageResponseDTO(pageRequestDTO, dtoList, results.getTotalElements());
    }

    private Page<Board> getSearchResults(String categoryName, String filterType, String keyword, Pageable pageable) {
        if (CATEGORY_ALL.equals(categoryName)) {
            return searchAllCategories(filterType, keyword, pageable);
        } else {
            return searchSpecificCategory(categoryName, filterType, keyword, pageable);
        }
    }

    /**
     * 전체 카테고리 검색
     */
    private Page<Board> searchAllCategories(String filterType, String keyword, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        switch (filterType) {
            case "content":
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("content", keyword)
                        .analyzer("ngram_analyzer"));
                break;
            case "writer":
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("nickname", keyword)
                        .analyzer("ngram_analyzer"));
                break;
            case "titleOrContent":
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("title", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchPhraseQuery("content", keyword)
                                .analyzer("ngram_analyzer"));
                break;
            case "titleOrWriter":
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("title", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchPhraseQuery("nickname", keyword)
                                .analyzer("ngram_analyzer"));
                break;
            case "contentOrWriter":
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("content", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchPhraseQuery("nickname", keyword)
                                .analyzer("ngram_analyzer"));
                break;
            case "titleOrContentOrWriter":
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("title", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchPhraseQuery("content", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchPhraseQuery("nickname", keyword)
                                .analyzer("ngram_analyzer"));
                break;
            case "title":
            default:
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("title", keyword)
                        .analyzer("ngram_analyzer"));
        }

        boolQueryBuilder.mustNot(QueryBuilders.termQuery("categoryName", NOTICE));

        queryBuilder.withQuery(boolQueryBuilder);
        queryBuilder.withPageable(pageable);

        SearchHits<Board> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), Board.class);
        List<Board> boardList = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(boardList, pageable, searchHits.getTotalHits());
    }

    /**
     * 특정 카테고리 조회
     */
    private Page<Board> searchSpecificCategory(String categoryName, String filterType, String keyword, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        switch (filterType) {
            case "content":
                boolQueryBuilder.must(QueryBuilders.matchQuery("content", keyword)
                        .analyzer("ngram_analyzer"));
                break;
            case "writer":
                boolQueryBuilder.must(QueryBuilders.matchQuery("nickname", keyword)
                        .analyzer("ngram_analyzer"));
                break;
            case "titleOrContent":
                boolQueryBuilder.should(QueryBuilders.matchQuery("title", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchQuery("content", keyword)
                                .analyzer("ngram_analyzer"));
                break;
            case "titleOrWriter":
                boolQueryBuilder.should(QueryBuilders.matchQuery("title", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchQuery("nickname", keyword)
                                .analyzer("ngram_analyzer"));
                break;
            case "contentOrWriter":
                boolQueryBuilder.should(QueryBuilders.matchQuery("content", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchQuery("nickname", keyword)
                                .analyzer("ngram_analyzer"));
                break;
            case "titleOrContentOrWriter":
                boolQueryBuilder.should(QueryBuilders.matchQuery("title", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchQuery("content", keyword)
                                .analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchQuery("nickname", keyword)
                                .analyzer("ngram_analyzer"));
                break;
            case "title":
            default:
                boolQueryBuilder.must(QueryBuilders.matchQuery("title", keyword)
                        .analyzer("ngram_analyzer"));
        }


        boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", categoryName));

        queryBuilder.withQuery(boolQueryBuilder);
        queryBuilder.withPageable(pageable);

        SearchHits<Board> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), Board.class);
        List<Board> boardList = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(boardList, pageable, searchHits.getTotalHits());
    }


    /**
     * 동의/유의어 검색
     */
    @Override
    public PageResponseDTO<SearchDTO> searchWithSynonyms(PageRequestDTO pageRequestDTO, String keyword) {
        Pageable pageable = commonUtils.createPageable(pageRequestDTO);
        Query searchQuery = buildSynonymSearchQuery(keyword, pageable);
        SearchHits<Board> searchHits = executeSynonymSearch(searchQuery);

        List<SearchDTO> synonymDtoList = mapSearchHitsToDTO(searchHits);
        return commonUtils.createPageResponseDTO(pageRequestDTO, synonymDtoList, searchHits.getTotalHits());
    }

    /**
     * 유의어 검색 쿼리
     */
    private Query buildSynonymSearchQuery(String keyword, Pageable pageable) {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "synonym_title", "synonym_content")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .analyzer("synonym_ngram_analyzer"))
                .withPageable(pageable)
                .build();
    }

    /**
     * 쿼리 실행
     */
    private SearchHits<Board> executeSynonymSearch(Query searchQuery) {
        return elasticsearchRestTemplate.search(searchQuery, Board.class);
    }


    private List<SearchDTO> mapSearchHitsToDTO(SearchHits<Board> searchHits) {
        return commonUtils.mapToDTOList(searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList()), SearchDTO.class);
    }


    /**
     * 기본 검색 + 유의어 검색
     */
    @Override
    public SearchPageResponseDTO searchWithStandardAndSynonyms(PageRequestDTO standardPageRequest, PageRequestDTO synonymPageRequest, String categoryName, String filterType, String keyword) {
        PageResponseDTO<SearchDTO> standardResult = searchByCategoryAndFilter(standardPageRequest, categoryName, filterType, keyword);
        PageResponseDTO<SearchDTO> synonymResult = searchWithSynonyms(synonymPageRequest, keyword);

        return SearchPageResponseDTO.builder()
                .StandardResult(standardResult)
                .SynonymResult(synonymResult)
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
     * 검색 순위 계산
     */
    @Override
    public List<RankingDTO> getRankingChanges() {
        List<String> currnetRankings = getRanking("now-6h/h", "now"); // 현재 순위 가져오기
        List<String> pastRankins = getRanking("now-24h/h", "now-1h/h");  // 이전 순위 가져오기

        List<RankingDTO> changes = new ArrayList<>();
        Set<String> processedKeywords = new HashSet<>(); // 이미 처리된 키워드를 추적

        // 현재 순위 처리
        for (int i = 0; i < currnetRankings.size(); i++) {
            String currentKeyword = currnetRankings.get(i);
            int pastIndex = pastRankins.indexOf(currentKeyword);
            String symbol = (pastIndex == -1 || i < pastIndex) ? "+" : (i > pastIndex) ? "-" : "_"; // 기본적으로 변동 없음을 의미하는 언더스코어로 설정

            changes.add(new RankingDTO(i + 1, currentKeyword, symbol));
            processedKeywords.add(currentKeyword);
        }

        // 이전 순위에 있었지만 현재 순위에 없는 키워드 처리
        int rankCounter = currnetRankings.size() + 1;
        for (int i = 0; i < pastRankins.size(); i++) {
            String pastKeyword = pastRankins.get(i);
            if (!processedKeywords.contains(pastKeyword)) {
                changes.add(new RankingDTO(rankCounter++, pastKeyword, "-"));
            }
        }

        // 최종 결과의 길이가 10을 초과하지 않도록 제한
        if (changes.size() > 10) {
            changes = changes.subList(0, 10);
        }

        return changes;
    }

    /**
     * 특정 기간의 순위
     */
    private List<String> getRanking(String from, String to) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("timestamp").gte(from).lt(to))
                .addAggregation(AggregationBuilders.terms("popular_keywords").field("keyword").size(10).order(BucketOrder.count(false)))
                .build();

        // 검색 실행 및 집계 결과 추출
        SearchHits<SearchKeyword> searchHits = elasticsearchRestTemplate.search(searchQuery, SearchKeyword.class, IndexCoordinates.of("search_keywords"));
        Aggregations aggregations = searchHits.getAggregations();
        Terms popularKeywords = aggregations.get("popular_keywords");

        // 집계 데이터 추출
        return popularKeywords.getBuckets().stream()
                .map(Terms.Bucket::getKeyAsString)
                .collect(Collectors.toList());

    }


    /**
     * 주간 조회수 기준 상위 7개 인기글
     */
    @Override
    public List<DailyBestBoardDTO> getTop5BoardsByViews() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime startOfDay = now.minusDays(7).toLocalDate().atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime endOfDay = now.toLocalDate().atStartOfDay(ZoneId.of("UTC")).plusDays(1);

        Query searchQuery = buildTop5BoardsSearchQuery(startOfDay, endOfDay);
        SearchHits<Board> searchHits = executeTop5BoardsSearch(searchQuery);

        List<DailyBestBoardDTO> result = mapToDailyBestBoardDTOs(searchHits);
        log.info("Search Results: " + result);

        return result;
    }

    /**
     * 조회수 기준 상위 7개 인기글 검색 쿼리 생성
     */
    private Query buildTop5BoardsSearchQuery(ZonedDateTime startOfDay, ZonedDateTime endOfDay) {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.rangeQuery("reg_date")
                                .gte(startOfDay)
                                .lt(endOfDay))
                        .must(QueryBuilders.termQuery("is_deleted", false)))
                .withSort(SortBuilders.fieldSort("views").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 7))
                .build();
    }

    /**
     * 조회수 기준 상위 7개 인기글 검색 쿼리 실행
     */
    private SearchHits<Board> executeTop5BoardsSearch(Query searchQuery) {
        return elasticsearchRestTemplate.search(searchQuery, Board.class);
    }

    /**
     * 검색 결과를 DailyBestBoardDTO로 매핑
     */
    private List<DailyBestBoardDTO> mapToDailyBestBoardDTOs(SearchHits<Board> searchHits) {
        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    Board board = hit.getContent();
                    return new DailyBestBoardDTO(board.getId(), board.getTitle(), board.getReplyCount(), board.getViews(), board.getLikesCount());
                })
                .collect(Collectors.toList());
    }


    /**
     * 연관 게시물 2개 조회
     */
    @Override
    public List<SuggestedBoardDTO> getSuggestedBoards(Long currentBoardId, String title, String content) {
        Query searchQuery = buildSuggestedBoardSearchQuery(title, content);
        SearchHits<Board> searchHits = executeSuggestedBoardSearch(searchQuery);

        return filterAndMapSuggestedBoards(searchHits, currentBoardId, 2);
    }

    /**
     * 연관 게시물 검색 쿼리 생성
     */
    private Query buildSuggestedBoardSearchQuery(String title, String content) {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("title", title).analyzer("ngram_analyzer"))
                        .should(QueryBuilders.matchQuery("content", content).analyzer("ngram_analyzer")))
                .withSort(SortBuilders.fieldSort("views").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 8)) // 일단 8개를 가져오고 나중에 필터링
                .build();
    }

    /**
     * 연관 게시물 검색 쿼리 실행
     */
    private SearchHits<Board> executeSuggestedBoardSearch(Query searchQuery) {
        return elasticsearchRestTemplate.search(searchQuery, Board.class);
    }

    /**
     * 검색 결과에서 현재 게시물 제외 및 DTO 매핑
     */
    private List<SuggestedBoardDTO> filterAndMapSuggestedBoards(SearchHits<Board> searchHits, Long currentBoardId, int limit) {
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .filter(board -> !board.getId().equals(currentBoardId)) // 현재 게시물을 제외
                .limit(limit) // 필터링 후 지정된 개수만 반환
                .map(board -> commonUtils.mapObject(board, SuggestedBoardDTO.class))
                .collect(Collectors.toList());
    }

}