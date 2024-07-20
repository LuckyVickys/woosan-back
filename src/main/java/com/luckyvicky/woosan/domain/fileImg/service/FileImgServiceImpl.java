package com.luckyvicky.woosan.domain.fileImg.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.luckyvicky.woosan.domain.fileImg.dto.FileUpdateDTO;
import com.luckyvicky.woosan.domain.fileImg.entity.FileImg;
import com.luckyvicky.woosan.domain.fileImg.repository.FileImgRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FileImgServiceImpl implements FileImgService {

    private final FileImgRepository fileImgRepository;
    private AmazonS3 s3;
    private String bucketName;

    public FileImgServiceImpl(
            FileImgRepository fileImgRepository,
            @Value("${cloud.ncp.s3.endpoint}") String endPoint,
            @Value("${cloud.ncp.region.static}") String regionName,
            @Value("${cloud.ncp.credentials.access-key}") String accessKey,
            @Value("${cloud.ncp.credentials.secret-key}") String secretKey,
            @Value("${cloud.ncp.s3.bucket-name}") String bucketName) {

        this.fileImgRepository = fileImgRepository;
        this.bucketName = bucketName;

        this.s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    @Override
    public void fileUploadMultiple(String type, Long targetId, List<MultipartFile> files) {
        for (int i = 0; i < files.size(); i++) {
            String uuid = UUID.randomUUID().toString();
            String uniqueSaveName = uuid + "_" + files.get(i).getOriginalFilename();

            FileImg fileImg = FileImg.builder()
                    .type(type)
                    .targetId(targetId)
                    .uuid(uuid)
                    .fileName(files.get(i).getOriginalFilename())
                    .ord(i)
                    .path("/" + type)
                    .build();
            fileImgRepository.save(fileImg);

            try {
                uploadMultipartFile(s3, uniqueSaveName, files.get(i), type);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to upload file: " + files.get(i), e);
            }
        }
    }

    private void uploadMultipartFile(AmazonS3 s3, String objectName, MultipartFile file, String type) throws IOException {
        String savePath = bucketName + "/" + type;
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            s3.putObject(savePath, objectName, file.getInputStream(), metadata);
            makeFilePublic(s3, objectName, savePath);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    private void makeFilePublic(AmazonS3 s3, String objectName, String savePath) {
        try {
            s3.setObjectAcl(savePath, objectName, CannedAccessControlList.PublicRead);
            System.out.format("Object %s has been made public.\n", objectName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> findFiles(String type, Long targetId) {
        List<FileImg> fileImgs = fileImgRepository.findByTypeAndTargetIdOrderByOrdAsc(type, targetId);

        return fileImgs.stream()
                .map(fileImg -> s3.getUrl(bucketName + fileImg.getPath(), fileImg.getUuid().toString() + "_" + fileImg.getFileName()).toString())
                .collect(Collectors.toList());
    }

    @Override
    public void targetFilesDelete(String type, Long targetId) {
        List<FileImg> fileImgs = fileImgRepository.findByTypeAndTargetIdOrderByOrdAsc(type, targetId);

        for (FileImg fileImg : fileImgs) {
            String key = fileImg.getUuid().toString() + "_" + fileImg.getFileName();
            s3.deleteObject(bucketName + fileImg.getPath(), key);
        }

        fileImgRepository.deleteByTypeAndTargetId(type, targetId);
    }

    @Override
    public void deleteS3FileByUrl(Long id, String type, String beforeFile) {
        String getKey = extractKeyFromUrl(beforeFile, type);
        List<FileImg> existFiles = fileImgRepository.findByTypeAndTargetIdOrderByOrdAsc(type, id);
        Map<Long, String> targetMap = new HashMap<>();

        String getFileName = getFileNameInUrl(beforeFile);

        for (FileImg fileImg : existFiles) {
            try {
                String encodedFileName = URLEncoder.encode(fileImg.getFileName(), StandardCharsets.UTF_8.toString());
                encodedFileName = encodedFileName.replace("+", "%20");
                targetMap.put(fileImg.getId(), fileImg.getUuid() + "_" + encodedFileName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<Long, String> entry : targetMap.entrySet()) {
            System.out.println(entry.getValue());
            try {
                String decodedEntryValue = URLDecoder.decode(entry.getValue(), StandardCharsets.UTF_8.toString());
                if (decodedEntryValue.equals(getFileName)) {
                    FileImg targetImg = fileImgRepository.findById(entry.getKey()).get();
                    fileImgRepository.deleteById(entry.getKey());
                    s3.deleteObject(bucketName, type + "/" + targetImg.getUuid() + "_" + targetImg.getFileName());
                    break;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateMainBanner(FileUpdateDTO fileUpdateDTO) {
        if (fileUpdateDTO.getExistFiles() == null) {
            targetFilesDelete("admin", 0L);
        } else {
            List<String> beforeFiles = findFiles("admin", 0L);
            List<String> afterFiles = fileUpdateDTO.getExistFiles();

            for (String beforeFile : beforeFiles) {
                if (!afterFiles.contains(beforeFile)) {
                    deleteS3FileByUrl(0L, "admin", beforeFile);
                }
            }
        }

        if (fileUpdateDTO.getNewFiles() != null) {
            fileUploadMultiple("admin", 0L, fileUpdateDTO.getNewFiles());
        }

    }

    private String getFileNameInUrl(String url) {
        try {
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
            return decodedUrl.substring(decodedUrl.lastIndexOf('/') + 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractKeyFromUrl(String fileUrl, String type) {
        String parseType = type + "/";
        String prefix = "woosan/" + parseType;
        int startIndex = fileUrl.indexOf(prefix) + prefix.length() - parseType.length();
        if (startIndex == -1) {
            throw new IllegalArgumentException("URL does not contain the expected prefix: " + prefix);
        }
        return fileUrl.substring(startIndex);
    }
}
