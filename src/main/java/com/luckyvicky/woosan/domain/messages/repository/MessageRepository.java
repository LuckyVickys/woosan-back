package com.luckyvicky.woosan.domain.messages.repository;

import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.messages.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findBySenderAndDelBySender(Member sender, Pageable pageable, Boolean delBySender);
    Page<Message> findByReceiverAndDelByReceiver(Member receiver, Pageable pageable, Boolean delByReceiver);
}
