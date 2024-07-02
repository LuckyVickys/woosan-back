package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.entity.QMatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.QMemberMatching;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberMatchingRepositoryCustomImpl implements  MemberMatchingRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public MemberMatchingRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
        @Override
        public long countByMemberIdAndType(Long memberId, int matchingType) {
            QMemberMatching memberMatching = QMemberMatching.memberMatching;
            QMatchingBoard matchingBoard = QMatchingBoard.matchingBoard;

            return queryFactory.selectFrom(memberMatching)
                    .join(memberMatching.matchingBoard, matchingBoard)
                    .where(memberMatching.member.id.eq(memberId)
                            .and(matchingBoard.matchingType.eq(matchingType)))
                    .fetchCount();
        }

        @Override
        public long countPendingByMemberIdAndType(Long memberId, int matchingType) {
            QMemberMatching memberMatching = QMemberMatching.memberMatching;
            QMatchingBoard matchingBoard = QMatchingBoard.matchingBoard;

            return queryFactory.selectFrom(memberMatching)
                    .join(memberMatching.matchingBoard, matchingBoard)
                    .where(memberMatching.member.id.eq(memberId)
                            .and(matchingBoard.matchingType.eq(matchingType))
                            .and(memberMatching.isAccepted.isNull()))
                    .fetchCount();
        }

        @Override
        public List<MemberMatching> findPendingByMemberIdAndType(Long memberId, int matchingType) {
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