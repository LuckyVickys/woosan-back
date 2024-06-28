//package com.luckyvicky.woosan.repository;
//
//import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
//import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
//import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
//import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
//import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
//import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
//import com.luckyvicky.woosan.domain.matching.service.MatchingBoardService;
//import com.luckyvicky.woosan.domain.member.entity.*;
//import com.luckyvicky.woosan.domain.member.repository.MemberProfileRepository;
//import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.IntStream;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@Log4j2
//@SpringBootTest
//public class MatchingBoardRepositoryTests {
//
//    @Autowired
//    private MatchingBoardRepository matchingBoardRepository;
//
//    @Autowired
//    private MatchingBoardService matchingBoardService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private MemberProfileRepository memberProfileRepository;
//
//    @BeforeEach
//    public void setup() {
//        // 테스트에 사용할 5명의 회원 엔티티 생성
//        IntStream.rangeClosed(1, 5).forEach(i -> {
//            Member member = memberRepository.save(Member.builder()
//                    .email("test" + System.currentTimeMillis() + i + "@woosan.com")
//                    .nickname("testuser" + System.currentTimeMillis() + i)
//                    .password("password")
//                    .point(0)
//                    .nextPoint(100)
//                    .memberType(MemberType.USER)
//                    .level(MemberType.Level.valueOf("LEVEL_" + i))
//                    .isActive(true)
//                    .socialType(SocialType.NORMAL)
//                    .build());
//            testMembers.add(member);
//        });
//    @Autowired
//    private MemberMatchingRepository memberMatchingRepository;
//
//    @Test
//    public void testAddAndRetrieveMatchingBoard() {
//        // Create a new member
//        Member member = Member.builder()
//                .email("test@example.com")
//                .nickname("nickname")
//                .password("password")
//                .point(0L)
//                .memberType(MemberType.USER)
//                .level(MemberType.Level.LEVEL_1)
//                .isActive(true)
//                .socialType(SocialType.NORMAL)
//                .build();
//        memberRepository.save(member);
//
//        // Create a new member profile
//        MemberProfile memberProfile = MemberProfile.builder()
//                .member(member)
//                .phone("010-1234-5678")
//                .location("Seoul")
//                .introduce("Hello, this is a test profile.")
//                .mbti(MBTI.INTJ)
//                .gender("Male")
//                .age(30)
//                .height(175)
//                .build();
//        memberProfileRepository.save(memberProfile);
//
//        // Create a new matching board
//        MatchingBoard matchingBoard = MatchingBoard.builder()
//                .member(member)
//                .matchingType(1)
//                .title("Test Matching Board")
//                .content("This is a test matching board.")
//                .placeName("Test Place")
//                .locationX(BigDecimal.valueOf(37.5665))
//                .locationY(BigDecimal.valueOf(126.9780))
//                .address("Test Address")
//                .meetDate(LocalDateTime.now().plusDays(1))
//                .tag("Test Tag")
//                .headCount(5)
//                .regDate(LocalDateTime.now())
//                .views(0)
//                .isDeleted(false)
//                .build();
//        matchingBoardRepository.save(matchingBoard);
//
//        // Create a new member matching
//        MemberMatching memberMatching = MemberMatching.builder()
//                .matchingBoard(matchingBoard)
//                .member(member)
//                .isAccepted(true)
//                .isManaged(false)
//                .build();
//        memberMatchingRepository.save(memberMatching);
//
//        // Retrieve and assert the matching board
//        Optional<MatchingBoard> retrievedBoard = matchingBoardRepository.findById(matchingBoard.getId());
//        assert retrievedBoard.isPresent();
//        assert retrievedBoard.get().getTitle().equals("Test Matching Board");
//        log.info("Test for adding and retrieving matching board completed.");
//
//        // Retrieve and assert the member profile
//        Optional<MemberProfile> retrievedProfile = memberProfileRepository.findById(memberProfile.getId());
//        assert retrievedProfile.isPresent();
//        assert retrievedProfile.get().getIntroduce().equals("Hello, this is a test profile.");
//        log.info("Test for adding and retrieving member profile completed.");
//
//        // Retrieve and assert the member matching
//        Optional<MemberMatching> retrievedMatching = memberMatchingRepository.findById(memberMatching.getId());
//        assert retrievedMatching.isPresent();
//        assert retrievedMatching.get().getMember().equals(member);
//        assert retrievedMatching.get().getMatchingBoard().equals(matchingBoard);
//        log.info("Test for adding and retrieving member matching completed.");
//    }
//
//    @Test
//    public void testAddMatchingBoardReply() {
//        // Create a new member
//        Member member = Member.builder()
//                .email("testreply@example.com")
//                .nickname("nickname")
//                .password("password")
//                .point(0L)
//                .memberType(MemberType.USER)
//                .level(MemberType.Level.LEVEL_1)
//                .isActive(true)
//                .socialType(SocialType.NORMAL)
//                .build();
//        memberRepository.save(member);
//
//        // Create a new matching board
//        MatchingBoard matchingBoard = MatchingBoard.builder()
//                .member(member)
//                .matchingType(1)
//                .title("Test Matching Board for Reply")
//                .content("This is a test matching board for reply.")
//                .placeName("Test Place")
//                .locationX(BigDecimal.valueOf(37.5665))
//                .locationY(BigDecimal.valueOf(126.9780))
//                .address("Test Address")
//                .meetDate(LocalDateTime.now().plusDays(1))
//                .tag("Test Tag")
//                .headCount(5)
//                .regDate(LocalDateTime.now())
//                .views(0)
//                .isDeleted(false)
//                .build();
//        matchingBoardRepository.save(matchingBoard);
//
//        // Create a new reply
//        MatchingBoardReply reply = MatchingBoardReply.builder()
//                .matchingId(matchingBoard.getId())
//                .parentId(0L) // Root reply, no parent
//                .regDate(LocalDateTime.now())
//                .content("This is a test reply.")
//                .writer("Test Writer")
//                .build();
//        matchingBoardReplyRepository.save(reply);
//
//        // Retrieve and assert the reply
//        Optional<MatchingBoardReply> retrievedReply = matchingBoardReplyRepository.findById(reply.getId());
//        assert retrievedReply.isPresent();
//        assert retrievedReply.get().getContent().equals("This is a test reply.");
//        log.info("Test for adding and retrieving matching board reply completed.");
//    }
//}
