package com.luckyvicky.woosan.domain.matching.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchingBoardMapper {

    @Mapping(source = "member.id", target = "memberId") // member의 id를 memberId로 매핑
    @Mapping(source = "tag", target = "tag", qualifiedByName = "stringToMap") // tag를 문자열에서 맵으로 변환하여 매핑
    MatchingBoardResponseDTO toResponseDTO(MatchingBoard matchingBoard);

    @Mapping(source = "memberId", target = "member.id") // memberId를 member의 id로 매핑
    @Mapping(source = "tag", target = "tag", qualifiedByName = "mapToString") // tag를 맵에서 문자열로 변환하여 매핑
    MatchingBoard toEntity(MatchingBoardRequestDTO requestDTO);

    @Named("stringToMap")
    default Map<String, String> stringToMap(String tag) {
        if (tag == null || tag.isEmpty()) {
            return new HashMap<>();
        }
        try {
            // JSON 문자열을 맵으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(tag, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Named("mapToString")
    default String mapToString(Map<String, String> tag) {
        if (tag == null || tag.isEmpty()) {
            return "";
        }
        try {
            // 맵을 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(tag);
        } catch (JsonProcessingException e) {
            // 예외 처리
            e.printStackTrace();
            return "";
        }
    }
}
