package com.luckyvicky.woosan.domain.matching.entity;

import com.luckyvicky.woosan.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matching_board_reply")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MatchingBoardReply {
    // 댓글 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 댓글 내용
    @Column(name = "content", nullable = false)
    private String content;

    // 작성자와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;  // 작성자

    // 작성일자
    @Column(name = "reg_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime regDate;

    // 부모 댓글 ID (답글의 경우)
    @Column(name = "parent_id", nullable = true, insertable = false, updatable = false)
    private Long parentId;

    // 부모 댓글과의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private MatchingBoardReply parent;

    // 매칭 보드와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id", nullable = false)
    private MatchingBoard matchingBoard;

    // 자식 댓글과의 관계 설정
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchingBoardReply> childReplies = new ArrayList<>();
}
