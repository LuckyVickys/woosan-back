package com.luckyvicky.woosan.domain.board.repository.elasticsearch;

import com.luckyvicky.woosan.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticsearchBoardRepository extends ElasticsearchRepository<Board, Long> {

    // 제목에 키워드가 포함된 게시물 검색
    Page<Board> findByTitleContainingAndCategoryNameNot(String title, String categoryName, Pageable pageable);

    // 내용에 키워드가 포함된 게시물 검색
    Page<Board> findByContentContainingAndCategoryNameNot(String content, String categoryName, Pageable pageable);

    // 작성자에 키워드가 포함된 게시물 검색
    Page<Board> findByNicknameContainingAndCategoryNameNot(String nickname, String categoryName, Pageable pageable);

    // 제목이나 내용에 키워드가 포함된 게시물 검색
    Page<Board> findByTitleContainingOrContentContainingAndCategoryNameNot(String title, String content, String categoryName, Pageable pageable);

    // 제목이나 작성자에 키워드가 포함된 게시물 검색
    Page<Board> findByTitleContainingOrNicknameContainingAndCategoryNameNot(String title, String nickname, String categoryName, Pageable pageable);

    // 내용이나 작성자에 키워드가 포함된 게시물 검색
    Page<Board> findByContentContainingOrNicknameContainingAndCategoryNameNot(String content, String nickname, String categoryName, Pageable pageable);

    // 제목, 내용, 작성자에 키워드가 모두 포함된 게시물 검색
    Page<Board> findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryNameNot(String title, String content, String nickname, String categoryName, Pageable pageable);

    // 제목에 키워드가 포함된 특정 카테고리의 게시물 검색
    Page<Board> findByTitleContainingAndCategoryName(String title, String categoryName, Pageable pageable);

    // 내용에 키워드가 포함된 특정 카테고리의 게시물 검색
    Page<Board> findByContentContainingAndCategoryName(String content, String categoryName, Pageable pageable);

    // 작성자에 키워드가 포함된 특정 카테고리의 게시물 검색
    Page<Board> findByNicknameContainingAndCategoryName(String nickname, String categoryName, Pageable pageable);

    // 제목이나 내용에 키워드가 포함된 특정 카테고리의 게시물 검색
    Page<Board> findByTitleContainingOrContentContainingAndCategoryName(String title, String content, String categoryName, Pageable pageable);

    // 제목이나 작성자에 키워드가 포함된 특정 카테고리의 게시물 검색
    Page<Board> findByTitleContainingOrNicknameContainingAndCategoryName(String title, String nickname, String categoryName, Pageable pageable);

    // 내용이나 작성자에 키워드가 포함된 특정 카테고리의 게시물 검색
    Page<Board> findByContentContainingOrNicknameContainingAndCategoryName(String content, String nickname, String categoryName, Pageable pageable);

    // 제목, 내용, 작성자에 키워드가 모두 포함된 특정 카테고리의 게시물 검색
    Page<Board> findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryName(String title, String content, String nickname, String categoryName, Pageable pageable);

    // 공지사항을 제외한 모든 카테고리에서 제목, 내용, 작성자(닉네임) 모두로 검색
    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"nickname\": {\"query\": \"?0\"}}}], \"must_not\": [{\"term\": {\"category_name\": \"공지사항\"}}]}}\"")
    List<Board> autocompleteWriter(String keyword);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"nickname\": {\"query\": \"?0\"}}}, {\"term\": {\"category_name\": \"?1\"}}]}}")
    List<Board> autocompleteWriterAndCategoryName(String writer, String categoryName);

    @Query("{\"bool\": {\"should\": [{\"wildcard\": {\"title\": \"*?0*\"}}, {\"wildcard\": {\"korean_title\": \"*?0*\"}}], \"must_not\": [{\"term\": {\"category_name\": \"공지사항\"}}]}}")
    List<Board> findByTitleOrKoreanTitleContainingAndCategoryNameNot(String keyword);

    @Query("{\"bool\": {\"should\": [{\"wildcard\": {\"content\": \"*?0*\"}}, {\"wildcard\": {\"korean_content\": \"*?0*\"}}], \"must_not\": [{\"term\": {\"category_name\": \"공지사항\"}}]}}")
    List<Board> findByContentOrKoreanContentContainingAndCategoryNameNot(String keyword);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"category_name\": \"?2\"}}], \"should\": [{\"wildcard\": {\"title\": \"*?0*\"}}, {\"wildcard\": {\"korean_title\": \"*?1*\"}}], \"minimum_should_match\": 1}}")
    List<Board> findByTitleContainingOrKoreanTitleContainingAndCategoryNameEquals(String titleKeyword, String koreanTitleKeyword, String categoryName);
    @Query("{\"bool\": {\"must\": [{\"match\": {\"category_name\": \"?2\"}}], \"should\": [{\"wildcard\": {\"content\": \"*?0*\"}}, {\"wildcard\": {\"korean_content\": \"*?1*\"}}], \"minimum_should_match\": 1}}")
    List<Board> findByContentContainingOrKoreanContentContainingAndCategoryNameEquals(String keyword, String keyword1, String categoryName);
}
