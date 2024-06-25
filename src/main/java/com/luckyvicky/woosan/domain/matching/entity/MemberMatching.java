package com.luckyvicky.woosan.domain.matching.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member_matching")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberMatching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "matcing_id", nullable =false)
    private Long matchingId;

    @Column(name = "member_id", nullable =false)
    private Long memberId;

    @Column(name = "is_accepted", nullable =false)
    private Boolean isAccepted;

    @Column(name = "is_managed", nullable =false)
    private Boolean isManaged;


}
