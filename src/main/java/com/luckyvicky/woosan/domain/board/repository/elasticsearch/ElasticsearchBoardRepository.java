package com.luckyvicky.woosan.domain.board.repository.elasticsearch;

import com.luckyvicky.woosan.domain.board.entity.Board;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticsearchBoardRepository extends ElasticsearchRepository<Board, Long> {

    // 특정 카테고리에서 제목으로 검색
    List<Board> findByCategoryNameAndTitleContaining(String categoryName, String title);

    // 특정 카테고리에서 내용으로 검색
    List<Board> findByCategoryNameAndContentContaining(String categoryName, String content);

    // 특정 카테고리에서 작성자(닉네임)로 검색
    List<Board> findByCategoryNameAndNicknameContaining(String categoryName, String nickname);

    // 특정 카테고리에서 제목 또는 내용으로 검색
    List<Board> findByCategoryNameAndTitleContainingOrContentContaining(String categoryName, String title, String content);

    // 특정 카테고리에서 제목과 작성자(닉네임)로 검색
    List<Board> findByCategoryNameAndTitleContainingAndNicknameContaining(String categoryName, String title, String nickname);

    // 특정 카테고리에서 내용과 작성자(닉네임)로 검색
    List<Board> findByCategoryNameAndContentContainingAndNicknameContaining(String categoryName, String content, String nickname);

    // 특정 카테고리에서 제목, 내용, 작성자(닉네임) 모두로 검색
    List<Board> findByCategoryNameAndTitleContainingAndContentContainingAndNicknameContaining(String categoryName, String title, String content, String nickname);

    // 공지사항을 제외한 모든 카테고리에서 제목으로 검색
    List<Board> findByCategoryNameNotAndTitleContaining(String excludedCategory, String title);

    // 공지사항을 제외한 모든 카테고리에서 내용으로 검색
    List<Board> findByCategoryNameNotAndContentContaining(String excludedCategory, String content);

    // 공지사항을 제외한 모든 카테고리에서 작성자(닉네임)로 검색
    List<Board> findByCategoryNameNotAndNicknameContaining(String excludedCategory, String nickname);

    // 공지사항을 제외한 모든 카테고리에서 제목 또는 내용으로 검색
    List<Board> findByCategoryNameNotAndTitleContainingOrContentContaining(String excludedCategory, String title, String content);

    // 공지사항을 제외한 모든 카테고리에서 제목과 작성자(닉네임)로 검색
    List<Board> findByCategoryNameNotAndTitleContainingAndNicknameContaining(String excludedCategory, String title, String nickname);

    // 공지사항을 제외한 모든 카테고리에서 내용과 작성자(닉네임)로 검색
    List<Board> findByCategoryNameNotAndContentContainingAndNicknameContaining(String excludedCategory, String content, String nickname);

    // 공지사항을 제외한 모든 카테고리에서 제목, 내용, 작성자(닉네임) 모두로 검색
    List<Board> findByCategoryNameNotAndTitleContainingAndContentContainingAndNicknameContaining(String excludedCategory, String title, String content, String nickname);
}
