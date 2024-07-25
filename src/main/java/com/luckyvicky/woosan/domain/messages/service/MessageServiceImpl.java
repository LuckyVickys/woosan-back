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
        Member sender = findMemberById(messageAddDTO.getSenderId());
        Member receiver = findMemberByNickname(messageAddDTO.getReceiver());
        validateMessageContent(messageAddDTO.getContent());

        Message message = createMessage(sender, receiver, messageAddDTO.getContent());
        Message savedMessage = messageRepository.save(message);

        return savedMessage.getId();
    }

    // id로 멤버를 찾기
    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 닉네임으로 멤버를 찾기
    private Member findMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 메시지 내용 검증
    private void validateMessageContent(String content) {
        if (content.trim().isEmpty()) {
            throw new GlobalException(ErrorCode.NULL_OR_BLANK);
        }
    }

    // 메시지 객체 생성
    private Message createMessage(Member sender, Member receiver, String content) {
        return Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .delBySender(false)
                .delByReceiver(false)
                .build();
    }
}
