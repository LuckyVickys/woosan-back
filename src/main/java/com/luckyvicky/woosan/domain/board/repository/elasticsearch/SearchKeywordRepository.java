package com.luckyvicky.woosan.domain.board.repository.elasticsearch;

import com.luckyvicky.woosan.domain.board.entity.SearchKeyword;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchKeywordRepository extends ElasticsearchRepository<SearchKeyword, String> {
    void save(SearchKeyword searchKeyword);
}