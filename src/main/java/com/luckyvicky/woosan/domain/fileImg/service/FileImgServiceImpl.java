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
import com.luckyvicky.woosan.domain.fileImg.entity.FileImg;
import com.luckyvicky.woosan.domain.fileImg.repository.FileImgRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileImgServiceImpl implements FileImgService {


    private final FileImgRepository fileImgRepository;

    private String endPoint = "https://kr.object.ncloudstorage.com";
    private String regionName = "kr-standard";
    private String accessKey = "1EDE4BFB56570D2EF3F0";
    private String secretKey = "C77834083E8F1B6F52187691A9E848B4BEE8E073";
    private String bucketName = "woosan";

    @Override
    public void fileUploadMultiple(String type, Long targetId, List<MultipartFile> files) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

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
            System.out.format("Object %s has been uploaded.\n", objectName);
            makeFilePublic(s3, objectName, savePath);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    private void makeFilePublic(AmazonS3 s3, String objectName, String savePath) {

        try {
            // 파일에 대한 ACL을 설정하여 공개로 설정
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

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        return fileImgs.stream()
                .map(fileImg -> s3.getUrl(bucketName + fileImg.getPath(),  fileImg.getUuid().toString() + "_" + fileImg.getFileName()).toString())
                .collect(Collectors.toList());
    }
}
