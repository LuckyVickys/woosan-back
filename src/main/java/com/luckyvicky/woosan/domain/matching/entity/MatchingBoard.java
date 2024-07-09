package com.luckyvicky.woosan.domain.matching.entity;

import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "matching_board")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MatchingBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //Member와의 관계 설정
    @JoinColumn(name = "member_id", nullable = false)
    private Member  member;

    @Column(name = "matching_type", nullable = false)
    private int matchingType; // 1: 정기 모임, 2: 번개, 3: 셀프 소개팅

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
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

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "head_count", nullable = false)
    private int headCount;

    //셀프 소개팅 작성 시 MemberProfile 데이터를 기반으로 필드를 채움
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = true)
    private MemberProfile profile;

    //모임장인지 확인하는 메소드
    public boolean isManager(Long memberId){
        return member.getId().equals(memberId);
    }

}

