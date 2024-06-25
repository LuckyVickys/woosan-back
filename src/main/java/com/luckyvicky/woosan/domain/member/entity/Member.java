package com.luckyvicky.woosan.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@AllArgsConstructor // USER 사용
@NoArgsConstructor
@ToString
@DynamicUpdate
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @ColumnDefault("0")
    private Long point;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    private MemberType.Level level;

    @ColumnDefault("true")
    private Boolean isActive;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    // level 필드를 제외한 생성자(ADMIN, GUEST 사용)
    public Member(String email, String nickname, String password, Long point, MemberType memberType, Boolean isActive, SocialType socialType) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.point = point;
        this.memberType = memberType;
        this.isActive = isActive;
        this.socialType = socialType;
    }

    // 게시물, 댓글 작성 시 포인트 추가
    public void addPoint(int points){
        this.point += points;
    }

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }
}
