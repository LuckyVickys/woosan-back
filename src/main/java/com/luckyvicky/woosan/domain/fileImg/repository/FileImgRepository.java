package com.luckyvicky.woosan.domain.fileImg.repository;

import com.luckyvicky.woosan.domain.fileImg.entity.FileImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileImgRepository extends JpaRepository<FileImg, Long> {


    List<FileImg> findByTypeAndTargetIdOrderByOrdAsc(String type, Long targetId);
}