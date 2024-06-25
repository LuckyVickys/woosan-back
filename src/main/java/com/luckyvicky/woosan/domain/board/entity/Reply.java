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
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 댓글 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;  // 게시글 고유번호

    @Column(nullable = false, length = 255)
    private String content;  // 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writerId", nullable = false)
    private Member writer;  // 작성자

    @CreationTimestamp
    private LocalDateTime regDate;  // 작성 날짜

    @Column
    private Long parentId;  // 부모 댓글
    
    @ColumnDefault("0")
    private int likesCount; // 추천 수


    public void changeContent(String content) {
        this.content = content;
    }

    /**
     * 추천수 증가
     */
    public void incrementLikesCount(){
        this.likesCount++;
    }

    /**
     * 추천수 감소
     */
    public void decrementLikesCount(){
        this.likesCount--;
    }
}
