package com.luckyvicky.woosan.domain.myPage.entity;

import jakarta.persistence.*;

@Entity
public class MyPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String nickname;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Long point;

    @Column(nullable = false)
    private Long grade;

    @Column(nullable = false)
    private Boolean isActive;

    // 기본 생성자
    public MyPage() {
    }

    // 모든 필드를 포함한 생성자
    public MyPage(String email, String nickname, String password, Long point, Long grade, Boolean isActive) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.point = point;
        this.grade = grade;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
