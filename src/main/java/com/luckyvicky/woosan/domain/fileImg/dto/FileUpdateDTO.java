package com.luckyvicky.woosan.domain.fileImg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUpdateDTO {
    List<String> existFiles;
    List<MultipartFile> newFiles;

}
