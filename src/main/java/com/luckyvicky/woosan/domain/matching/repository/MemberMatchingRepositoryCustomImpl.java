package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.entity.QMatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.QMemberMatching;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberMatchingRepositoryCustomImpl implements MemberMatchingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public MemberMatchingRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    // 특정 매칭 타입의 승인된 멤버 수 확인
    @Override
    public long countByMemberIdAndMatchingBoard_MatchingType(Long memberId, int matchingType) {
        QMemberMatching memberMatching = QMemberMatching.memberMatching;
        QMatchingBoard matchingBoard = QMatchingBoard.matchingBoard;

        return queryFactory.selectFrom(memberMatching)
                .join(memberMatching.matchingBoard, matchingBoard)
                .where(memberMatching.member.id.eq(memberId)
                        .and(matchingBoard.matchingType.eq(matchingType))
                        .and(memberMatching.isAccepted.isTrue()))
                .fetchCount();
    }

    // 특정 타입의 대기 중인 멤버 수 확인
    @Override
    public long countPendingByMemberIdAndMatchingBoard_MatchingType(Long memberId, int matchingType) {
        QMemberMatching memberMatching = QMemberMatching.memberMatching;
        QMatchingBoard matchingBoard = QMatchingBoard.matchingBoard;

        return queryFactory.selectFrom(memberMatching)
                .join(memberMatching.matchingBoard, matchingBoard)
                .where(memberMatching.member.id.eq(memberId)
                        .and(matchingBoard.matchingType.eq(matchingType))
                        .and(memberMatching.isAccepted.isNull()))
                .fetchCount();
    }

    // 특정 타입의 대기 중인 요청 가져오기
    @Override
    public List<MemberMatching> findPendingByMemberIdAndMatchingBoard_MatchingType(Long memberId, int matchingType) {
        QMemberMatching memberMatching = QMemberMatching.memberMatching;
        QMatchingBoard matchingBoard = QMatchingBoard.matchingBoard;

        return queryFactory.selectFrom(memberMatching)
                .join(memberMatching.matchingBoard, matchingBoard)
                .where(memberMatching.member.id.eq(memberId)
                        .and(matchingBoard.matchingType.eq(matchingType))
                        .and(memberMatching.isAccepted.isNull()))
                .fetch();
    }
}
