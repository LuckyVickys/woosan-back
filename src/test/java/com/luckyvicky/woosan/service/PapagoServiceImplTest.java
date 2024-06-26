package com.luckyvicky.woosan.service;

import com.cybozu.labs.langdetect.LangDetectException;
import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.service.PapagoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PapagoServiceImplTest {

    @Autowired
    private PapagoServiceImpl papagoService;

    @Test
    public void testStringDetector() throws LangDetectException {
        String text = "这是一个测试句子 한국어";
        String detectedLang = papagoService.stringDetector(text);
        System.out.println("=============================================");
        System.out.println(detectedLang);
        System.out.println("=============================================");

        //한국어  ko
        //일본어 ja
        //중국어 zh-cn -> cn
        //영어 en

    }

    @Test
    public void translateBoard() throws IOException, LangDetectException {
        BoardDTO dto = new BoardDTO();
//        dto.setContent("안녕하세요");
//        dto.setTitle("감사합니다");
//        dto.setContent("Thank You");
//        dto.setTitle("Hello");

        dto.setContent("こんにちは");
        dto.setTitle("ありがとうございます");

        BoardDTO result = papagoService.tanslateBoardDetailPage(dto);

        System.out.println("=============================================");
        System.out.println(result.getTitle());
        System.out.println(result.getContent());
        System.out.println("=============================================");

    }
}
