package com.luckyvicky.woosan.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamicUpdate
public class MemberProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String phone;

    @Column
    private String location;

    @Column
    private String introduce;

    @Column
    @Enumerated(EnumType.STRING)
    private MBTI mbti;

    @Column
    private String gender;

    @Column
    private int age;

    @Column
    private int height;

    // 추가된 setter 메소드
    public void setMember(Member member) {
        this.member = member;
    }
}
