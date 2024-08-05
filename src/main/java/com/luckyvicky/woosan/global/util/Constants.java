package com.luckyvicky.woosan.global.util;

import java.util.List;

public interface Constants {
    public static final int MAX_TITLE_LENGTH = 40;
    public static final int MAX_CONTENT_LENGTH = 1960; // 게시물 내용 최대 길이
    public static final int MAX_REPLY_CONTENT_LENGTH = 1000;  // 댓글 내용 최대 길이
    public static final String NOTICE = "공지사항";
    public static final List<String> VALID_CATEGORIES = List.of("맛집", "청소", "요리", "재테크", "인테리어", "정책", "기타", "공지사항");

    public static final List<String> VALID_NOTICE = List.of("공지사항");

    public static final String TYPE_BOARD = "게시물";
    public static final String TYPE_REPLY = "댓글";

    public static final String CATEGORY_ALL = "전체";

}
