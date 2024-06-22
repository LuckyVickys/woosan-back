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

    @Column
    private String nickname;

    @Column(nullable = false)
    private String password;

    @ColumnDefault("0")
    private Long point;

    @ColumnDefault("1")
    private Long grade;

    @ColumnDefault("true")
    private Boolean isActive;
}
