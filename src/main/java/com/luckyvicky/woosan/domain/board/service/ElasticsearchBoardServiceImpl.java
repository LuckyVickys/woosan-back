package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.repository.elasticsearch.ElasticsearchBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ElasticsearchBoardServiceImpl implements ElasticsearchBoardService {

    private final ElasticsearchBoardRepository elasticsearchBoardRepository;

    @Override
    public List<Board> searchByCategoryAndFilter(String categoryName, String filterType, String keyword) {
        // categoryName이 null인 경우 "전체"로 기본 설정
        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = "전체";
        }

        if (categoryName.equals("전체")) {
            switch (filterType) {
                case "title":
                    return elasticsearchBoardRepository.findByTitleContainingAndCategoryNameNot(keyword, "공지사항");
                case "content":
                    return elasticsearchBoardRepository.findByContentContainingAndCategoryNameNot(keyword, "공지사항");
                case "writer":
                    return elasticsearchBoardRepository.findByNicknameContainingAndCategoryNameNot(keyword, "공지사항");
                case "titleOrContent":
                    return elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryNameNot(keyword, keyword, "공지사항");
                case "titleOrWriter":
                    return elasticsearchBoardRepository.findByTitleContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, "공지사항");
                case "contentOrWriter":
                    return elasticsearchBoardRepository.findByContentContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, "공지사항");
                case "titleOrContentOrWriter":
                    return elasticsearchBoardRepository.findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryNameNot(keyword, keyword, keyword, "공지사항");
                default:
                    return elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryNameNot(keyword, keyword, "공지사항");
            }
        } else {
            switch (filterType) {
                case "title":
                    return elasticsearchBoardRepository.findByTitleContainingAndCategoryName(keyword, categoryName);
                case "content":
                    return elasticsearchBoardRepository.findByContentContainingAndCategoryName(keyword, categoryName);
                case "writer":
                    return elasticsearchBoardRepository.findByNicknameContainingAndCategoryName(keyword, categoryName);
                case "titleOrContent":
                    return elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryName(keyword, keyword, categoryName);
                case "titleOrWriter":
                    return elasticsearchBoardRepository.findByTitleContainingOrNicknameContainingAndCategoryName(keyword, keyword, categoryName);
                case "contentOrWriter":
                    return elasticsearchBoardRepository.findByContentContainingOrNicknameContainingAndCategoryName(keyword, keyword, categoryName);
                case "titleOrContentOrWriter":
                    return elasticsearchBoardRepository.findByTitleContainingOrContentContainingOrNicknameContainingAndCategoryName(keyword, keyword, keyword, categoryName);
                default:
                    return elasticsearchBoardRepository.findByTitleContainingOrContentContainingAndCategoryName(keyword, keyword, categoryName);
            }
        }
    }

    public List<String> autocomplete(String keyword, String searchType) {
        List<Board> result;
        if ("제목".equals(searchType)) {
            System.out.println("title auto");
            result = elasticsearchBoardRepository.autocompleteTitle(keyword);
            return result.stream()
                    .map(Board::getTitle)
                    .distinct()
                    .collect(Collectors.toList());
        } else if ("내용".equals(searchType)) {
            System.out.println("content auto");
            result = elasticsearchBoardRepository.findByContentContaining(keyword);
            return result.stream()
                    .map(Board::getContent)
                    .distinct()
                    .collect(Collectors.toList());
        } else if ("작성자".equals(searchType)) {
            System.out.println("writer auto");
            result = elasticsearchBoardRepository.autocompleteWriter(keyword);
            return result.stream()
                    .map(Board::getNickname)
                    .distinct()
                    .collect(Collectors.toList());
        }
        return List.of(); // 빈 리스트 반환
    }
}
