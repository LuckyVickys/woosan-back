package com.luckyvicky.woosan.domain.messages.repository;

import com.luckyvicky.woosan.domain.messages.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

}
