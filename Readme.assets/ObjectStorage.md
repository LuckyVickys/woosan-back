# NCP Object Storage
## NCP

### 1. Object Storage 이용 신청

NCP(Naver Cloud Platform) 콘솔을 통해 Object Storage를 이용 신청합니다.

### 2. Bucket 생성
- NCP Object Storage에서 Bucket을 생성합니다.
- Region 당 최소 1개의 Bucket이 보장됩니다.

### 파일 업로드
1. **파일 업로드**: 파일을 Object Storage에 업로드합니다.
2. **공개 설정**: 업로드된 파일을 공개 설정합니다.
3. **URL을 통한 이미지 뷰**: 생성된 URL을 통해 Object Storage에 저장된 이미지를 조회할 수 있습니다.

## Java를 활용한 Obejct Storage 사진 업로드
### 1. Gradle 의존성 추가
```java
implementation 'com.amazonaws:aws-java-sdk-s3:1.12.58'
```
### 2. properties 설정 (NCP 마이페이지 인증키 관리)
NCP 마이페이지에서 발급받은 접근키와 암호키를 application.properties 파일에 추가합니다:

```java
cloud.ncp.credentials.access-key= 접근키
cloud.ncp.credentials.secret-key= 암호키
cloud.ncp.region.static=kr-standard
cloud.ncp.s3.endpoint=https://kr.object.ncloudstorage.com
cloud.ncp.s3.bucket-name=woosan
```

### 3. Config 설정
Object Storage와의 연결을 위한 설정 클래스를 작성합니다:

```java
package com.luckyvicky.woosan.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectStorageConfig {
    @Value("${cloud.ncp.s3.endpoint}")
    private String endPoint;

    @Value("${cloud.ncp.region.static}")
    private String regionName;

    @Value("${cloud.ncp.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.ncp.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.ncp.s3.bucket-name}")
    private String bucketName;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    @Bean
    public String bucketName() {
        return bucketName;
    }
}
```


### 4. Service 의존성 주입
파일 업로드 서비스를 위한 의존성을 주입합니다:

```java
@Service
@Transactional
public class FileImgServiceImpl implements FileImgService {

    private final FileImgRepository fileImgRepository;
    private final AmazonS3 s3;
    private final String bucketName;

    public FileImgServiceImpl(FileImgRepository fileImgRepository, AmazonS3 s3, @Value("${cloud.ncp.s3.bucket-name}") String bucketName) {
        this.fileImgRepository = fileImgRepository;
        this.s3 = s3;
        this.bucketName = bucketName;
    }

}
```

### 4. AmazonS3 함수
파일 업로드, 공개 설정, 삭제 및 URL 조회를 위한 AmazonS3 함수를 활용합니다:


```java
//파일 업로드
s3.putObject(savePath, objectName, file.getInputStream(), metadata);
//파일 공개 설정
s3.setObjectAcl(savePath, objectName, CannedAccessControlList.PublicRead);
//파일 삭제
s3.deleteObject(bucketName, type + "/" + targetImg.getUuid() + "_" + targetImg.getFileName());
//파일 Url 조회
s3.getUrl(bucketName + fileImg.getPath(), fileImg.getUuid().toString() + "_" + fileImg.getFileName()).toString())
```
