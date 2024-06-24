package com.luckyvicky.woosan.domain.fileImg.controller;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fileImg/")
public class FileImgController {

    @Autowired
    FileImgService fileImgService;




}
