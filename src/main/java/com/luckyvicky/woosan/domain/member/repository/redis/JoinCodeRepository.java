package com.luckyvicky.woosan.domain.member.repository.redis;

import com.luckyvicky.woosan.domain.member.entity.JoinCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinCodeRepository extends CrudRepository<JoinCode, String> {
}
