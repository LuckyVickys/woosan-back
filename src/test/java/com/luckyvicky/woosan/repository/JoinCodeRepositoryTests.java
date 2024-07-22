package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.member.entity.JoinCode;
import com.luckyvicky.woosan.domain.member.repository.JoinCodeRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class JoinCodeRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JoinCodeRepository joinCodeRepository;

    @Test
    public void testAddJoinCode() {
        JoinCode code = new JoinCode("WOOSAN0730?!", "woosan@naver.com");

        JoinCode savedCode = joinCodeRepository.save(code);
        log.info("joinCode: " + savedCode.getCode());
    }
}
