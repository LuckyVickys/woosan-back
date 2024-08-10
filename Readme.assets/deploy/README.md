# Spring Boot CICD 
![image](https://github.com/user-attachments/assets/fe7f198e-7ad7-4cfe-af42-3b854e6f6166)
## CI/CD Pipeline 과정
1. **GitHub**
   - `Git push` 발생 시 Webhook을 통해 Jenkins 서버로 코드가 전송됩니다.

2. **Jenkins**
   - Webhook을 통해 GitHub에서 코드를 가져와서 클론(Git Clone)합니다.
   - 백엔드(BE) 빌드 작업을 수행합니다.
   - 빌드된 애플리케이션을 Docker 이미지를 생성하고, Docker Hub에 푸시(docker img push)합니다.

3. **Docker Hub**
   - Jenkins에서 푸시한 Docker 이미지를 저장합니다.

4. **Spring Boot 서버**
   - Docker Hub로부터 이미지를 풀(pull)하여 애플리케이션을 실행합니다.
## Spring Boot 설정
### 1. Dockerfile
Dockerfile은 애플리케이션을 실행할 환경을 정의하고, 이를 기반으로 Docker 이미지를 빌드하는 설정 파일입니다.
```java
FROM openjdk:17-jdk-slim

# 한국 시간대 설정
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    dpkg-reconfigure -f noninteractive tzdata
    
# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일과 리소스 파일을 복사
COPY build/libs/woosan-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
```
### 2. gardle
 Gradle 빌드 프로세스의 일부로 포함되어 프로젝트를 빌드할 때 자동으로 실행됩니다.
```java
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.0'
    id 'io.spring.dependency-management' version '1.1.5'
    id 'com.github.node-gradle.node' version '3.2.1'
}

node {
    version = '16.13.0'
    npmVersion = '8.1.0'
    download = true
    workDir = file("${project.buildDir}/nodejs")
    npmWorkDir = file("${project.buildDir}/npm")
}

task cleanNpmCache(type: NpmTask) {
    args = ['cache', 'clean', '--force']
}

task appNpmInstall(type: NpmTask) {
    workingDir = file("${project.projectDir}/src/main/resources/static")
    args = ['install']
}

appNpmInstall.dependsOn(cleanNpmCache)
build.dependsOn(appNpmInstall)

tasks.withType(Test) {
    enabled = false
}

```
## Jenkins 설정
### General
![image](https://github.com/user-attachments/assets/fffc06d5-60ff-4193-a44a-c04d1292c681)
### 소스 코드 관리
![image](https://github.com/user-attachments/assets/d67573c3-9346-4fca-9bae-edee1d9b2399)
- GitHub Credentials 등록
![image](https://github.com/user-attachments/assets/94ed6a7f-a893-4a71-8407-c0e361339a4a)
### 빌드 유발
![image](https://github.com/user-attachments/assets/0c03295f-460f-44e5-bced-5531d0f12039)
### 빌드 환경
GitHub에 올리지 않은 파일을 Secret file파일 등록 후 파일 변수명 지정
![image](https://github.com/user-attachments/assets/f579069e-016f-442f-ba74-49c68210008a)
- Secret file 등록
![scf](https://github.com/user-attachments/assets/d030035f-6545-4cac-9333-97c42e80bbae)

빌드 이후 배포서버에서 실행될 내용
### 스프링부트 배포 서버에 public key 등록

배포서버에서 vi 편집기 실행

```bash
root@springboot-svr:~# mkdir .ssh
root@springboot-svr:~# vi .ssh/authorized_keys
```

### Publish Over SSH 플러그인 설정

플러그인 설치

- Jenkins 관리
  - 플러그인 관리
    - `Available plugins` 탭
    - `Publish Over SSH` 플러그인 설치

플러그인 연동

- Jenkins 관리
  - 시스템 설정
    - Publish over SSH
      - Passphrase: 스프링부트서버 암호
      - 추가 버튼 클릭
      - SSH Servers
        - Name: 임의의 서버 이름
        - Hostname: 스프링부트서버의 IP 주소
        - Username: 사용자 아이디
        - `Test Configuration` 버튼 클릭
        - `Success` OK!
![image](https://github.com/user-attachments/assets/7b4fecea-1b7b-4b11-8bb1-de7f33f468ad)
![image](https://github.com/user-attachments/assets/bd31a624-f87b-460a-aec1-aa0310558ce1)
![image](https://github.com/user-attachments/assets/33fcda0c-eac5-43b7-95ee-5765e1295938)

### Build Steps
빌드전 Secret file파일 복사
![cp](https://github.com/user-attachments/assets/2e69a2e4-461f-4072-a76a-c98da34082d1)
빌드
![grd](https://github.com/user-attachments/assets/799a8d9d-bb45-4668-9fb7-bc556801618d)
Docker img 생성 및 DockerHub에 push
![image](https://github.com/user-attachments/assets/ec62244c-80bd-4d94-8847-c773284f92ba)

## GitHub 설정

### github webhook 연동
- Repository/Settings/Webhooks
  - `Add webhook` 클릭
  - Payload URL: `http://젠킨스서버주소:8080/github-webhook/`
  - Content type: `application/json`
  - 저장
![image](https://github.com/user-attachments/assets/7e6a921f-fe4e-4516-93b5-dd053bd10243)

