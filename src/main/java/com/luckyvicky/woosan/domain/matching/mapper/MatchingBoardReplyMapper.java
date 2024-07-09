package com.luckyvicky.woosan.domain.matching.mapper;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchingBoardReplyMapper {

    MatchingBoardReplyResponseDTO toResponseDTO(MatchingBoardReply matchingBoardReply);

    MatchingBoardReply toEntity(MatchingBoardReplyRequestDTO requestDTO);
}
