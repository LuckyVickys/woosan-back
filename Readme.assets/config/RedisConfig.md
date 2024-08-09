# Redis Configuration 레디스 DB 설정

## 1. build.gradle 설정
Redis 의존성을 추가합니다.

```java
dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-data-redis'
	implementation 'org.springframework.session:spring-session-data-redis'
}
```

## 2. application-repo.properties 설정
Redis DB의 정보를 작성하였습니다.

```java
spring.data.redis.host=**********
spring.data.redis.port=6379
spring.data.redis.password=**********
```

### 3. RedisConfig 클래스
Redis DB와의 연결을 설정하고, Redis와 상호작용하기 위한 RedisTemplate을 정의합니다. <br>
이를 통해 Redis 서버에 데이터를 저장하고 조회하며 삭제하는 등의 다양한 작업을 수행할 수 있습니다.

```java
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
```
