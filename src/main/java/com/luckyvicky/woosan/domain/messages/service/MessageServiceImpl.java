package com.luckyvicky.woosan.domain.messages.service;

import com.luckyvicky.woosan.global.exception.MemberException;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.domain.messages.dto.MessageAddDTO;
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
    public Long add(MessageAddDTO messageAddDTO) {

        try {
            Member sender = memberRepository.findById(messageAddDTO.getSenderId())
                    .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

            Member receiver = memberRepository.findByNickname(messageAddDTO.getReceiver())
                    .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

            if(messageAddDTO.getContent().trim().isEmpty()) {
                throw new GlobalException(ErrorCode.NULL_OR_BLANK);
            }

            Message message = Message.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content(messageAddDTO.getContent())
                    .delBySender(false)
                    .delByReceiver(false)
                    .build();

            Message msg = messageRepository.save(message);

            return msg.getId();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
