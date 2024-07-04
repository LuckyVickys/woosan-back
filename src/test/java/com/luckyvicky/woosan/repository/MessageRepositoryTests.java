package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.domain.messages.entity.Message;
import com.luckyvicky.woosan.domain.messages.repository.MessageRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Log4j2
@SpringBootTest
public class MessageRepositoryTests {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testAddMessage() {

        Member sender = memberRepository.findById(1L).orElseThrow();
        Member receiver = memberRepository.findById(2L).orElseThrow();

        IntStream.rangeClosed(1, 5).forEach(i -> {
            Message message = Message.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content("message " + i)
                    .regDate(LocalDateTime.now())
                    .delBySender(false)
                    .delByReceiver(false)
                    .build();

            log.info("message_id " + messageRepository.save(message).getId());
        });
    }

}
