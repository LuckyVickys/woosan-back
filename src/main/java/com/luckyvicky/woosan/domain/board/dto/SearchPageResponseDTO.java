package com.luckyvicky.woosan.domain.board.dto;

import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchPageResponseDTO {
    private PageResponseDTO<SearchDTO> StandardResult;
    private PageResponseDTO<SearchDTO> SynonymResult;
}
