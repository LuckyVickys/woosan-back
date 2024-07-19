package com.luckyvicky.woosan.global.util;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Log4j2
public class CommonUtils {

    private final ModelMapper modelMapper;

    /**
     * 페이지 설정 생성
     */
    public Pageable createPageable(PageRequestDTO pageRequestDTO) {
        return PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").ascending());
    }

    /**
     * DTO 리스트 매핑
     */
    public <S, T> List<T> mapToDTOList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(source -> modelMapper.map(source, targetClass))
                .collect(Collectors.toList());
    }
    /**
     * 단일 객체 DTO 매핑
     */
    public <S, T> T mapObject(S source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }
}
