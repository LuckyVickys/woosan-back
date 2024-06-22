package com.luckyvicky.woosan.testServcie;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class FileImgServiceTests {

    @Autowired
    private FileImgService fileImgService;

    @Test
    void testMultiFilesUpload() throws IOException {

        String filePath1 = "C:\\Users\\ho976\\IdeaSnapshots\\OneDrive\\사진\\스크린샷\\20231002_Jang_Won-young_(장원영).jpg";
        String filePath2 = "C:\\Users\\ho976\\IdeaSnapshots\\OneDrive\\사진\\스크린샷\\스크린샷 2024-05-29 163620.png";

        MultipartFile multipartFile1 = new MockMultipartFile("file1", "20231002_Jang_Won-young_(장원영).jpg", "image/jpeg", new FileInputStream(filePath1));
        MultipartFile multipartFile2 = new MockMultipartFile("file2", "스크린샷 2024-05-29 163620.png", "image/png", new FileInputStream(filePath2));

        List<MultipartFile> files = Arrays.asList(multipartFile1, multipartFile2);
        String type = "board";
        Long targetId = 2L;

        fileImgService.fileUploadMultiple(type, targetId, files);
    }
    @Test
    void selectTargetFile() {
        String type = "board";
        Long targetId = 2L;

        List<String> filesURL = fileImgService.findFiles(type, targetId);
        System.out.println(filesURL);
    }
}
