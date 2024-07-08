package com.luckyvicky.woosan.domain.board.entity;

import com.luckyvicky.woosan.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Document(indexName = "board")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board {

    @jakarta.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writerId", nullable = false)
    @Field(type = FieldType.Object)
    private Member writer;  // 작성자 (회원 엔티티와의 연관관계)

    @Column(nullable = false, length = 40)
    @Field(type = FieldType.Text)
    private String title;  // 제목

    @Column(nullable = false, length = 1960)
    @Field(type = FieldType.Text)
    private String content;  // 내용

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime regDate;  // 작성 날짜

    @LastModifiedDate
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;  // 수정 날짜

    @Transient
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private String regDateString;

    @ColumnDefault("0")
    @Field(type = FieldType.Integer)
    private int views;  // 조회수

    @ColumnDefault("0")
    @Field(type = FieldType.Integer)
    private int likesCount; // 추천 수

    @ColumnDefault("false")
    @Field(type = FieldType.Boolean)
    private boolean isDeleted;  // 삭제 상태

    @Column(nullable = false, length = 255)
    @Field(type = FieldType.Text)
    private String categoryName;  // 카테고리 유형

    @ColumnDefault("0")
    @Field(type = FieldType.Integer)
    private int replyCount; // 댓글 수

    @PostLoad
    @PostPersist
    @PostUpdate
    public void updateRegDateString() {
        this.regDateString = this.regDate != null ? this.regDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    /**
     * 제목 수정
     */
    public void changeTitle(String title){
        this.title = title;
    }

    /**
     * 내용 수정
     */
    public void changeContent(String content){
        this.content = content;
    }

    /**
     * 게시글 삭제
     */
    public void changeIsDeleted(Boolean isDeleted){
        this.isDeleted = isDeleted;
    }

    /**
     * 조회수 추가
     */
    public void addViewCount(){
        this.views++;
    }

    // 추천수 변경
    public void changeLikesCount(int likesCount){
        this.likesCount += likesCount;
    }

    // 댓글수 변경
    public void changeReplyCount(int replyCount){
        this.replyCount += replyCount;
    }
}
