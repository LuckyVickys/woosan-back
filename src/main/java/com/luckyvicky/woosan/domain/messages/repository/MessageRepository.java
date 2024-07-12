package com.luckyvicky.woosan.domain.messages.repository;

import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.messages.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderId(Long senderId);
}