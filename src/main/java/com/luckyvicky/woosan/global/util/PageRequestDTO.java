package com.luckyvicky.woosan.global.util;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    public void validate() {
        if (page < 1) {
            throw new GlobalException(ErrorCode.PAGE_INDEX_INVALID);
        }
        if (size < 1) {
            throw new GlobalException(ErrorCode.PAGE_SIZE_INVALID);
        }
    }
}
