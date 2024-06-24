package com.luckyvicky.woosan.domain.fileImg.repository;

import com.luckyvicky.woosan.domain.fileImg.entity.FileImg;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileImgRepository extends JpaRepository<FileImg, Long> {


    List<FileImg> findByTypeAndTargetIdOrderByOrdAsc(String type, Long targetId);

    //게시글 row 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM FileImg f WHERE f.type = :type AND f.targetId = :targetId")
    void deleteByTypeAndTargetId(@Param("type") String type, @Param("targetId") Long targetId);


}