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

}
