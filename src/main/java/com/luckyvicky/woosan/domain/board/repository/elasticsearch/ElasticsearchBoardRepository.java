package com.luckyvicky.woosan.domain.board.repository.elasticsearch;

import com.luckyvicky.woosan.domain.board.entity.Board;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticsearchBoardRepository extends ElasticsearchRepository<Board, Long> {

    // 제목에 키워드가 포함된 게시물 검색
    List<Board> findByTitleContainingAndCategoryNameNot(String title, String categoryName);

    // 내용에 키워드가 포함된 게시물 검색
    List<Board> findByContentContainingAndCategoryNameNot(String content, String categoryName);

    // 작성자에 키워드가 포함된 게시물 검색
    List<Board> findByNicknameContainingAndCategoryNameNot(String nickname, String categoryName);

    // 제목이나 내용에 키워드가 포함된 게시물 검색
    List<Board> findByTitleContainingOrContentContainingAndCategoryNameNot(String title, String content, String categoryName);

    // 제목이나 작성자에 키워드가 포함된 게시물 검색
    List<Board> findByTitleContainingOrNicknameContainingAndCategoryNameNot(String title, String nickname, String categoryName);

    // 내용이나 작성자에 키워드가 포함된 게시물 검색
    List<Board> findByContentContainingOrNicknameContainingAndCategoryNameNot(String content, String nickname, String categoryName);

    // 제목, 내용, 작성자에 키워드가 모두 포함된 게시물 검색
    List<Board> findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryNameNot(String title, String content, String nickname, String categoryName);

    // 제목에 키워드가 포함된 특정 카테고리의 게시물 검색
    List<Board> findByTitleContainingAndCategoryName(String title, String categoryName);

    // 내용에 키워드가 포함된 특정 카테고리의 게시물 검색
    List<Board> findByContentContainingAndCategoryName(String content, String categoryName);

    // 작성자에 키워드가 포함된 특정 카테고리의 게시물 검색
    List<Board> findByNicknameContainingAndCategoryName(String nickname, String categoryName);

    // 제목이나 내용에 키워드가 포함된 특정 카테고리의 게시물 검색
    List<Board> findByTitleContainingOrContentContainingAndCategoryName(String title, String content, String categoryName);

    // 제목이나 작성자에 키워드가 포함된 특정 카테고리의 게시물 검색
    List<Board> findByTitleContainingOrNicknameContainingAndCategoryName(String title, String nickname, String categoryName);

    // 내용이나 작성자에 키워드가 포함된 특정 카테고리의 게시물 검색
    List<Board> findByContentContainingOrNicknameContainingAndCategoryName(String content, String nickname, String categoryName);

    // 제목, 내용, 작성자에 키워드가 모두 포함된 특정 카테고리의 게시물 검색
    List<Board> findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryName(String title, String content, String nickname, String categoryName);
}
