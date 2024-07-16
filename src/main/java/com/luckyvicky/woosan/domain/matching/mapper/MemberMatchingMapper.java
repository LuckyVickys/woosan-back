package com.luckyvicky.woosan.domain.matching.mapper;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMatchingMapper {
    MemberMatchingMapper INSTANCE = Mappers.getMapper(MemberMatchingMapper.class);

    // 요청 DTO를 엔티티로 변환
    @Mapping(target = "matchingBoard.id", source = "matchingId")
    @Mapping(target = "member.id", source = "memberId")
    MemberMatching toEntity(MemberMatchingRequestDTO requestDTO);

    // 엔티티를 응답 DTO로 변환
    @Mapping(target = "matchingId", source = "matchingBoard.id")
    @Mapping(target = "memberId", source = "member.id")
    MemberMatchingResponseDTO toDto(MemberMatching memberMatching);
}
