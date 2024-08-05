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

    //지역 변경
    public void setLocation(String location) {
        this.location = location;
    }

    //성별 변경
    public void setGender(String gender) {
        this.gender = gender;
    }

    //나이 변경
    public void setAge(int age) {
        this.age = age;
    }

    //키 변경
    public void setHeight(int height) {
        this.height = height;
    }

    //mbti 변경
    public void setMbti(MBTI mbti) {
        this.mbti = mbti;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    // 휴대폰 번호 변경
    public void setPhone(String phone) { this.phone = phone; }

}
