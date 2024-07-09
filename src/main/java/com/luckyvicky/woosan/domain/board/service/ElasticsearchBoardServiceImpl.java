package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.repository.elasticsearch.ElasticsearchBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ElasticsearchBoardServiceImpl implements ElasticsearchBoardService {

    private final ElasticsearchBoardRepository elasticsearchBoardRepository;

    @Override
    public List<Board> searchByCategoryAndFilter(String categoryName, String filterType, String keyword) {
        String excludedCategory = "공지사항";
        if (filterType.isEmpty()) {
            filterType = "titleOrContent"; // 기본 필터 타입 설정
        }
        if (keyword.isEmpty()) {
            keyword = ""; // 빈 검색어 처리
        }

        if ("전체".equals(categoryName) || categoryName.isEmpty()) {
            // "공지사항"을 제외하고 검색
            switch (filterType) {
                case "제목":
                    return elasticsearchBoardRepository.findByCategoryNameNotAndTitleContaining(excludedCategory, keyword);
                case "내용":
                    return elasticsearchBoardRepository.findByCategoryNameNotAndContentContaining(excludedCategory, keyword);
                case "작성자":
                    return elasticsearchBoardRepository.findByCategoryNameNotAndNicknameContaining(excludedCategory, keyword);
                case "제목 + 내용":
                    return elasticsearchBoardRepository.findByCategoryNameNotAndTitleContainingOrContentContaining(excludedCategory, keyword, keyword);
                case "제목 + 작성자":
                    return elasticsearchBoardRepository.findByCategoryNameNotAndTitleContainingAndNicknameContaining(excludedCategory, keyword, keyword);
                case "내용 + 작성자":
                    return elasticsearchBoardRepository.findByCategoryNameNotAndContentContainingAndNicknameContaining(excludedCategory, keyword, keyword);
                case "제목 + 내용 + 작성자":
                    return elasticsearchBoardRepository.findByCategoryNameNotAndTitleContainingAndContentContainingAndNicknameContaining(excludedCategory, keyword, keyword, keyword);
                default:
                    return elasticsearchBoardRepository.findByCategoryNameNotAndTitleContainingOrContentContaining(excludedCategory, keyword, keyword);
            }
        } else {
            // 특정 카테고리로 검색
            switch (filterType) {
                case "제목":
                    return elasticsearchBoardRepository.findByCategoryNameAndTitleContaining(categoryName, keyword);
                case "내용":
                    return elasticsearchBoardRepository.findByCategoryNameAndContentContaining(categoryName, keyword);
                case "작성자":
                    return elasticsearchBoardRepository.findByCategoryNameAndNicknameContaining(categoryName, keyword);
                case "제목 + 내용":
                    return elasticsearchBoardRepository.findByCategoryNameAndTitleContainingOrContentContaining(categoryName, keyword, keyword);
                case "제목 + 작성자":
                    return elasticsearchBoardRepository.findByCategoryNameAndTitleContainingAndNicknameContaining(categoryName, keyword, keyword);
                case "내용 + 작성자":
                    return elasticsearchBoardRepository.findByCategoryNameAndContentContainingAndNicknameContaining(categoryName, keyword, keyword);
                case "제목 + 내용 + 작성자":
                    return elasticsearchBoardRepository.findByCategoryNameAndTitleContainingAndContentContainingAndNicknameContaining(categoryName, keyword, keyword, keyword);
                default:
                    return elasticsearchBoardRepository.findByCategoryNameAndTitleContainingOrContentContaining(categoryName, keyword, keyword);
            }
        }
    }
}
