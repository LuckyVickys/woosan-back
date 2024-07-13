package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.member.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/myPage")
public class MypageController {
    private final MyPageService myPageService;

}
