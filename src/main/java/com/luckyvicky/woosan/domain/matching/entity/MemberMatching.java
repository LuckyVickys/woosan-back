package com.luckyvicky.woosan.domain.matching.entity;

import com.luckyvicky.woosan.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_matching")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberMatching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //매칭 보드와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id", nullable =false)
    private MatchingBoard matchingBoard;

    // 멤버와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //수락여부 (null: 대기중, true: 승인됨, false: 거절됨)
    @Column(name = "is_accepted", nullable = true)
    private Boolean isAccepted;

    //관리 여부
    @Column(name = "is_managed", nullable =false)
    private Boolean isManaged;


}
