package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.repository.MemberMatchingRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberMatchingRepositoryTests {

    @Autowired
    private MemberMatchingRepository memberMatchingRepository;

    @Test
    public void testAddMemberMatching() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            MemberMatching memberMatching = MemberMatching.builder()
                    .matchingId((long) i)
                    .memberId((long) i)
                    .isAccepted(false)
                    .isManaged(false)
                    .build();

            // Save the MemberMatching entity and log its ID
            MemberMatching savedMemberMatching = memberMatchingRepository.save(memberMatching);
            log.info("memberMatching_id: " + savedMemberMatching.getId());
        });
    }

    @Test
    public void testRead() {
        Long id = 1L;
        MemberMatching memberMatching = memberMatchingRepository.findById(id).orElse(null);
        log.info("--------------------");
        log.info(memberMatching);
    }
}
