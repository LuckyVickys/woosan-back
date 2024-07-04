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

    public void setLocation(String location) {
    }

    public void setGender(String gender) {
    }

    public void setAge(int age) {
    }

    public void setHeight(int height) {
    }

    public void setMbti(MBTI mbti) {
    }

}
