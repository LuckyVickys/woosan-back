package com.luckyvicky.woosan.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardService;
import com.luckyvicky.woosan.domain.member.entity.MBTI;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.entity.SocialType;
import com.luckyvicky.woosan.domain.member.repository.MemberProfileRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MatchingBoardServiceTest {

    @Autowired
    private MatchingBoardService matchingBoardService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberProfileRepository memberProfileRepository;

    @BeforeEach
    public void setup() {
        // 테스트 전에 DB 초기화 및 데이터 삭제
        memberProfileRepository.deleteAll();
        memberRepository.deleteAll();

        // 테스트 데이터 추가
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Member member = Member.builder()
                    .email("test" + i + "@example.com")
                    .nickname("nickname" + i)
                    .password("password")
                    .point(0L)
                    .memberType(MemberType.USER)
                    .isActive(true)
                    .socialType(SocialType.NORMAL)
                    .build();
            memberRepository.save(member);
        });
    }

    @Test
    public void testCreateSelfIntroduction() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Member member = memberRepository.findByEmail("test" + i + "@example.com");
            Optional<Member> optionalMember = Optional.ofNullable(member);
            if (optionalMember.isPresent()) {
                member = optionalMember.get();

                MatchingBoardRequestDTO requestDTO = MatchingBoardRequestDTO.builder()
                        .memberId(member.getId())
                        .matchingType(3)
                        .title("Self Introduction " + i)
                        .content("This is self introduction content " + i)
                        .placeName("Place " + i)
                        .locationX(BigDecimal.valueOf(37.5665 + i))
                        .locationY(BigDecimal.valueOf(126.9780 + i))
                        .address("Address " + i)
                        .meetDate(LocalDateTime.now().plusDays(i))
                        .tag("Tag " + i)
                        .headCount(1)
                        .location("Location " + i)
                        .introduce("Introduce " + i)
                        .mbti(MBTI.INFJ)
                        .gender("Gender " + i)
                        .age(20 + i)
                        .height(170 + i)
                        .build();

                MatchingBoard matchingBoard = matchingBoardService.createMatchingBoard(requestDTO);
                assertNotNull(matchingBoard);
                assertEquals(matchingBoard.getMember().getId(), member.getId());

                Optional<MemberProfile> optionalProfile = memberProfileRepository.findByMemberId(member.getId());
                if (optionalProfile.isPresent()) {
                    MemberProfile profile = optionalProfile.get();
                    assertNotNull(profile);
                    assertEquals(profile.getIntroduce(), "Introduce " + i);
                }
            }
        });
    }
}
