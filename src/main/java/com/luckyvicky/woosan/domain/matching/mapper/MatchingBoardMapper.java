package com.luckyvicky.woosan.domain.matching.mapper;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchingBoardMapper {

    MatchingBoardResponseDTO toResponseDTO(MatchingBoard matchingBoard);

    MatchingBoard toEntity(MatchingBoardRequestDTO requestDTO);
}
