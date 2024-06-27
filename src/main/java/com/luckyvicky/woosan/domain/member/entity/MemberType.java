package com.luckyvicky.woosan.domain.member.entity;

public enum MemberType {
    USER(Level.class), GUEST, ADMIN;

    // 레벨 클래스를 저장하기 위한 필드 (USER에만 적용됨)
    private Class<? extends Enum<?>> levelCLass;

    MemberType() {
        this.levelCLass = null;
    }

    MemberType(Class<? extends Enum<?>> levelCLass) {
        this.levelCLass = levelCLass;   // levelClass 초기화
    }

    public Class<? extends Enum<?>> getLevelCLass() {
        return levelCLass;  // 연관된 레벨 클래스 반환
    }

    public enum Level {
        LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_4, LEVEL_5, LEVEL1;
    }
}
