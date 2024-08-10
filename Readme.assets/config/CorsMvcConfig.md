# CORS 설정
## CorsMvcConfig
WebMvcConfigurer 인터페이스를 구현하고 이를 통하여 CORS 설정을 커스터마이즈하는 클래스입니다. <br>
클라이언트와 서버 간의 도메인이 달라서 발생하는 문제를 해결하기 위해 사용됩니다. <br> <br>
GET, POST, PUT, DELETE, OPTIONS, PATCH 메서드를 허용하며, <br>
브라우저는 자격 증명을 포함해 서버와의 요청을 보낼 수 있습니다. <br>
또한 클라이언트가 1시간 동안 캐시된 결과를 사용할 수 있도록 하여 <br>
같은 요청을 1시간 내에 다시 할 때 프리플라이트 요청을 다시 보내지 않도록 하여 성능을 최적화합니다.

```java
@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```
