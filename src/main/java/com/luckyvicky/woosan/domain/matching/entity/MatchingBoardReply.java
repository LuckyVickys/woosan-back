package com.luckyvicky.woosan.domain.matching.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matching_board_reply")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingBoardReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "writer", nullable = false)
    private String writer;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "parent_id", nullable = true)
    private Long parentId;

    @Column(name = "matching_id", nullable = false)
    private Long matchingId;

}
