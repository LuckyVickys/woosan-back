package com.luckyvicky.woosan.domain.matching.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "maching_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_type", nullable = false)
    private int postType;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "views", nullable = false)
    private int views;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Column(name = "location_x", nullable = false)
    private BigDecimal locationX;

    @Column(name = "location_y", nullable = false)
    private BigDecimal locationY;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "meet_date", nullable = false)
    private LocalDateTime meetDate;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "head_count", nullable = false)
    private int headCount;

}

