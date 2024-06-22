package com.luckyvicky.woosan.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@AllArgsConstructor
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

    @ColumnDefault("true")
    private Boolean isActive;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
}
