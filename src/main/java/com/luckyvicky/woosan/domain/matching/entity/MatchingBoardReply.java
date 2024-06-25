package com.luckyvicky.woosan.domain.matching.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "matching_board_reply")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingBoardReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "writer", nullable = false)
    private String writer;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "parentId", nullable = false)
    private Long parentId;

    @Column(name = "matching_id", nullable = false)
    private Long matchingId;

}
