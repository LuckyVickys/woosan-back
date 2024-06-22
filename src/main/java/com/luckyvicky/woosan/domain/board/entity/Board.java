package com.luckyvicky.woosan.domain.board.entity;

import com.luckyvicky.woosan.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writerId", nullable = false)
    private Member writer;  // 작성자 (회원 엔티티와의 연관관계)

    @Column(nullable = false)
    private int postType;  // 게시글 형태

    @Column(nullable = false, length = 255)
    private String title;  // 제목

    @Column(nullable = false, length = 255)
    private String content;  // 내용

    @CreationTimestamp
    private LocalDateTime regDate;  // 작성 날짜

    @ColumnDefault("0")
    private int views;  // 조회수

    @ColumnDefault("false")
    private Boolean isDeleted;  // 삭제 상태

    @Column(nullable = false, length = 255)
    private String categoryName;  // 카테고리 유형



    public void changeTitle(String title){
        this.title = title;
    }

    public void changeContent(String content){
        this.content = content;
    }

    public void changeIsDeleted(Boolean isDeleted){
        this.isDeleted = isDeleted;
    }

}
