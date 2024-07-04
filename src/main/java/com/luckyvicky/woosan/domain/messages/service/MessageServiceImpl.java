package com.luckyvicky.woosan.domain.messages.service;

import com.luckyvicky.woosan.global.exception.MemberException;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.domain.messages.dto.MessageDTO;
import com.luckyvicky.woosan.domain.messages.entity.Message;
import com.luckyvicky.woosan.domain.messages.repository.MessageRepository;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;

    // 쪽지 전송
    @Override
    public Long add(MessageDTO messageDTO) {

        try {
            Member sender = memberRepository.findById(messageDTO.getSenderId())
                    .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

            Member receiver = memberRepository.findById(messageDTO.getReceiverId())
                    .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

            if(messageDTO.getContent().trim().isEmpty()) {
                throw new GlobalException(ErrorCode.NULL_OR_BLANK);
            }

            Message message = Message.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content(messageDTO.getContent())
                    .build();

            message = messageRepository.save(message);

            return message.getId();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
