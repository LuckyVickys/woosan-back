# I. 패키지 구조
패키지 구조는 도메인형을 사용하여 도메인별 응집도를 높였습니다.
```
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃  ┗ 📂com.luckyvicky.woosan
 ┃ ┃    ┣ 📂domain
 ┃ ┃    ┃  ┣ 📂admin
 ┃ ┃    ┃  ┃  ┣ 📂controller
 ┃ ┃    ┃  ┃  ┣ 📂dto
 ┃ ┃    ┃  ┃  ┣ 📂repository
 ┃ ┃    ┃  ┃  ┗ 📂service
 ┃ ┃    ┃  ┣ 📂board
 ┃ ┃    ┃  ┃  ┣ 📂controller
 ┃ ┃    ┃  ┃  ┣ 📂dto
 ┃ ┃    ┃  ┃  ┣ 📂entity
 ┃ ┃    ┃  ┃  ┣ 📂exception
 ┃ ┃    ┃  ┃  ┣ 📂projection
 ┃ ┃    ┃  ┃  ┣ 📂repository
 ┃ ┃    ┃  ┃  ┗ 📂service
 ┃ ┃    ┃  ┣ 📂fileImg
 ┃ ┃    ┃  ┃  ┣ 📂dto
 ┃ ┃    ┃  ┃  ┣ 📂entity
 ┃ ┃    ┃  ┃  ┣ 📂repository
 ┃ ┃    ┃  ┃  ┣ 📂service
 ┃ ┃    ┃  ┃  ┗ 📂utils
 ┃ ┃    ┃  ┣ 📂likes
 ┃ ┃    ┃  ┃  ┣ 📂controller
 ┃ ┃    ┃  ┃  ┣ 📂dto
 ┃ ┃    ┃  ┃  ┣ 📂entity
 ┃ ┃    ┃  ┃  ┣ 📂exception
 ┃ ┃    ┃  ┃  ┣ 📂repository
 ┃ ┃    ┃  ┃  ┗ 📂service
 ┃ ┃    ┃  ┣ 📂matching
 ┃ ┃    ┃  ┃  ┣ 📂controller
 ┃ ┃    ┃  ┃  ┣ 📂dto
 ┃ ┃    ┃  ┃  ┣ 📂entity
 ┃ ┃    ┃  ┃  ┣ 📂exception
 ┃ ┃    ┃  ┃  ┣ 📂mapper
 ┃ ┃    ┃  ┃  ┣ 📂repository
 ┃ ┃    ┃  ┃  ┗ 📂service
 ┃ ┃    ┃  ┣ 📂member
 ┃ ┃    ┃  ┃  ┣ 📂controller
 ┃ ┃    ┃  ┃  ┣ 📂dto
 ┃ ┃    ┃  ┃  ┣ 📂entity
 ┃ ┃    ┃  ┃  ┣ 📂mapper
 ┃ ┃    ┃  ┃  ┣ 📂repository
 ┃ ┃    ┃  ┃  ┗ 📂service
 ┃ ┃    ┃  ┣ 📂messages
 ┃ ┃    ┃  ┃  ┣ 📂controller
 ┃ ┃    ┃  ┃  ┣ 📂dto
 ┃ ┃    ┃  ┃  ┣ 📂entity
 ┃ ┃    ┃  ┃  ┣ 📂mapper
 ┃ ┃    ┃  ┃  ┣ 📂repository
 ┃ ┃    ┃  ┃  ┗ 📂service
 ┃ ┃    ┃  ┗ 📂report
 ┃ ┃    ┃  ┃  ┣ 📂controller
 ┃ ┃    ┃  ┃  ┣ 📂dto
 ┃ ┃    ┃  ┃  ┣ 📂entity
 ┃ ┃    ┃  ┃  ┣ 📂mapper
 ┃ ┃    ┃  ┃  ┣ 📂repository
 ┃ ┃    ┃  ┃  ┣ 📂service
 ┃ ┃    ┃  ┃  ┗ 📂utils
 ┃ ┃    ┗ 📂global
 ┃ ┃    ┃  ┣ 📂annotation
 ┃ ┃    ┃  ┣ 📂aop
 ┃ ┃    ┃  ┣ 📂auth
 ┃ ┃    ┃  ┣ 📂config
 ┃ ┃    ┃  ┣ 📂exception
 ┃ ┃    ┃  ┗ 📂util
 ┃ ┃    ┗ 📜WoosanApplication
 ┃ ┗ 📂resources
 ┃    ┣ 📜application.properties
 ┃    ┣ 📜application-elk.yml
 ┃    ┣ 📜application-repo.properties
 ┃    ┗ 📜logback-access-spring.xml
 ┗ 📂test
   ┗ 📂java
      ┗ 📂com.luckyvicky.woosan
        ┣ 📂repository
        ┣ 📂service
        ┗ 📜WoosanApplicationTests
```
0. <a href="https://github.com/LuckyVickys/woosan-back/tree/main/Readme.assets/config">설정</a>
1. <a href="https://github.com/LuckyVickys/woosan-front/blob/main/Readme.assets/Deploy/deploy.md">배포</a>
2. <a href="https://github.com/LuckyVickys/woosan-front/tree/main/Readme.assets/member">회원 기능
3. <a href="https://github.com/LuckyVickys/woosan-front/tree/main/Readme.assets/board">게시글 기능</a>
4. <a href="https://github.com/LuckyVickys/woosan-front/tree/main/Readme.assets/matching">모임 기능</a>
5. <a href="https://github.com/LuckyVickys/woosan-back/blob/main/Readme.assets/message/AddMessage.md">쪽지 기능</a>
6. <a href="https://github.com/LuckyVickys/woosan-back/tree/main/Readme.assets/file">파일 기능</a>
