package com.luckyvicky.woosan.domain.fileImg.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileImgService {

    void fileUploadMultiple(String type, Long targetId, List<MultipartFile> files);
    List<String> findFiles(String type, Long targetId);

}
