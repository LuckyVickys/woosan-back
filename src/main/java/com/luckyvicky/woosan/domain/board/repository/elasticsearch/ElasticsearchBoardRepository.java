package com.luckyvicky.woosan.domain.board.repository.elasticsearch;

import com.luckyvicky.woosan.domain.board.entity.Board;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticsearchBoardRepository extends ElasticsearchRepository<Board, Long> {

    // 공지사항을 제외한 모든 카테고리에서 제목, 내용, 작성자(닉네임) 모두로 검색
    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"nickname\": {\"query\": \"?0\"}}}], \"must_not\": [{\"term\": {\"category_name\": \"공지사항\"}}]}}\"")
    List<Board> autocompleteWriter(String keyword);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"nickname\": {\"query\": \"?0\"}}}, {\"term\": {\"category_name\": \"?1\"}}]}}")
    List<Board> autocompleteWriterAndCategoryName(String writer, String categoryName);

    @Query("{\"bool\": {\"must_not\": [{\"term\": {\"category_name\": \"공지사항\"}}], \"should\": [?0]}}")
    List<Board> findByTitleOrKoreanTitleContainingAndCategoryNameNot(String shouldQuery);

    @Query("{\"bool\": {\"must_not\": [{\"term\": {\"category_name\": \"공지사항\"}}], \"should\": [?0]}}")
    List<Board> findByContentOrKoreanContentContainingAndCategoryNameNot(String shouldQuery);

    @Query("{\"bool\": {\"must\": [{\"term\": {\"category_name\": \"?1\"}}], \"should\": [?0], \"minimum_should_match\": 1}}")
    List<Board> findByTitleContainingOrKoreanTitleContainingAndCategoryNameEquals(String shouldQuery, String categoryName);

    @Query("{\"bool\": {\"must\": [{\"term\": {\"category_name\": \"?1\"}}], \"should\": [?0], \"minimum_should_match\": 1}}")
    List<Board> findByContentContainingOrKoreanContentContainingAndCategoryNameEquals(String shouldQuery, String categoryName);
}
