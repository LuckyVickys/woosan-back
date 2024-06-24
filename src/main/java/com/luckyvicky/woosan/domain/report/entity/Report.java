package com.luckyvicky.woosan.domain.report.entity;

import com.luckyvicky.woosan.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_member_id", nullable = false)
    private Member reporter;

    @Column(name = "type", length = 255, nullable = false)
    private String type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "complaint_reason", length = 255, nullable = false)
    private String complaintReason;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "is_checked", nullable = false)
    private Boolean isChecked;

    @ManyToOne
    @JoinColumn(name = "reported_member_id", nullable = false)
    private Member reportedMember;

    public void change(String complaintReason) {
        this.complaintReason = complaintReason;
    }
}
