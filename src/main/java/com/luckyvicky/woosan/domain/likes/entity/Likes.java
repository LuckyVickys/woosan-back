package com.luckyvicky.woosan.domain.likes.entity;

import com.luckyvicky.woosan.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId;

    @Column(name = "type", nullable = false, length = 255)
    private String type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

}
