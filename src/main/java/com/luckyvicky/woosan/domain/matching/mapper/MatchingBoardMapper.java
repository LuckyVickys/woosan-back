package com.luckyvicky.woosan.domain.matching.mapper;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchingBoardMapper {

    @Mapping(source = "member.id", target = "memberId") // member의 id를 memberId로 매핑
    MatchingBoardResponseDTO toResponseDTO(MatchingBoard matchingBoard);

    @Mapping(source = "memberId", target = "member.id") // memberId를 member의 id로 매핑
    MatchingBoard toEntity(MatchingBoardRequestDTO requestDTO);
}
