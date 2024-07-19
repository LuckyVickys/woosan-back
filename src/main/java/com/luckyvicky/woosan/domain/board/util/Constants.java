package com.luckyvicky.woosan.domain.board.util;

import java.util.List;

public interface Constants {
    public static final int MAX_TITLE_LENGTH = 40;
    public static final int MAX_CONTENT_LENGTH = 1960;
    public static final String NOTICE = "공지사항";
    public static final List<String> VALID_CATEGORIES = List.of("맛집", "청소", "요리", "재테크", "인테리어", "정책", "기타", "공지사항");
}
