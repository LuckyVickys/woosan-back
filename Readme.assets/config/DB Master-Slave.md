# Master Slave DB
![스크린샷 2024-08-09 173722](https://github.com/user-attachments/assets/b4c1f3d3-6ded-425f-af82-44810e91920a)
### 읽기/쓰기 분리
   - Master DB는 쓰기 작업(데이터 삽입, 업데이트, 삭제)을 처리하고, Slave DB는 읽기 작업(데이터 조회)을 처리하여 데이터베이스의 부하를 분산시킵니다.

## NCP

### 1. Cloud DB for MySQL 생성

NCP(Naver Cloud Platform) 콘솔을 통해 Cloud DB for MySQL을 생성합니다. 이때 **고가용성 보장** 옵션을 체크하여 Master DB와 StandBy DB를 생성합니다.

### 2. ACG 설정

- 접근 권한을 허용하기 위해 ACG 설정에서 `0.0.0.0/0` 포트 `3306`을 허용합니다.

### 3. Slave DB 생성

- Slave DB를 생성하여 Master DB, StandBy DB, Slave DB 총 3개의 DB를 구성합니다.

### 4. DB User 생성 및 권한 부여 (선택 사항)

- 필요에 따라 DB User를 생성하고 권한을 부여합니다.

## Java를 활용한 Master-Slave DB 설정

### 1. Gradle 의존성 추가

Gradle의 `build.gradle` 파일에 MySQL 드라이버 의존성을 추가합니다.

```java
	implementation 'mysql:mysql-connector-java:8.0.28'
```

### 2. properties 설정
application.properties 파일에 Master와 Slave 데이터 소스 설정을 추가합니다.
```java
spring.datasource.hikari.driver-class-name=com.mysql.cj.jdbc.Driver

# Master DataSource Configuration
spring.datasource.master.hikari.username=*****************
spring.datasource.master.hikari.password=*****************
spring.datasource.master.hikari.jdbc-url=jdbc:mysql://master-db-url:3306/dbname

# Slave DataSource Configuration
spring.datasource.slave.hikari.username=*****************
spring.datasource.slave.hikari.password=*****************
spring.datasource.slave.hikari.jdbc-url=jdbc:mysql://slave-db-url:3306/dbname
```

### 3. Annotation 생성
Master-Slave DB 구조에서 특정 메서드 또는 클래스가 Slave 데이터 소스를 사용하도록 지시하는 어노테이션을 생성합니다.
역할: 메서드나 클래스에 적용하여 해당 메서드 또는 클래스가 slave 데이터 소스를 사용하도록 지시하는 어노테이션.
```java
package com.luckyvicky.woosan.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SlaveDBRequest {
}

```
Annotatino 활용 예시: Service 함수에서 데이터베이스에 요청할 경우 Slave DB에 요청되도록 지정합니다. 
```java
    // 로그인 한 멤버 정보
    @SlaveDBRequest
    @Override
    public MemberInfoDTO getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email);
        if(member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        MemberInfoDTO dto = mapper.memberToMemberInfoDTO(member);
        List<String> profile = fileImgService.findFiles("member", member.getId());
        if(profile != null){
            dto.setProfile(profile);
        }
        return dto;
    }

```

### 4. Config 설정
Master-Slave 데이터베이스와의 연결을 위한 설정 클래스를 작성합니다.
```java
package com.luckyvicky.woosan.global.config.distinct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class DataSourceConfiguration {

    public static final String MASTER_DATASOURCE = "masterDataSource";
    public static final String SLAVE_DATASOURCE = "slaveDataSource";

    @Bean(MASTER_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.master.hikari")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(SLAVE_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Primary
    public DataSource routingDataSource() {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(MASTER_DATASOURCE, masterDataSource());
        dataSourceMap.put(SLAVE_DATASOURCE, slaveDataSource());
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource());
        return routingDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("routingDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

```
### 5.DataSourceContextHolder
현재 스레드의 데이터 소스 타입을 관리하기 위한 클래스를 작성합니다.
```java
package com.luckyvicky.woosan.global.config.distinct;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setDataSourceType(String dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }

    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }
}

```

### 6.RoutingDataSource
데이터 소스를 동적으로 라우팅하기 위한 클래스를 작성합니다.
```java
package com.luckyvicky.woosan.global.config.distinct;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceType();
    }
}

```

### 7.AOP를 통한 데이터 소스 전환
AOP를 사용하여 메서드 호출 전 데이터 소스를 설정하는 클래스를 작성합니다.
```java
package com.luckyvicky.woosan.global.aop;

import com.luckyvicky.woosan.global.config.distinct.DataSourceContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAspect {

    @Before("execution(* com.luckyvicky.woosan..*.service..*.*(..)) && @annotation(com.luckyvicky.woosan.global.annotation.SlaveDBRequest)")
    public void setReadDataSourceType() {
        DataSourceContextHolder.setDataSourceType("slaveDataSource");
    }

    @Before("execution(* com.luckyvicky.woosan..*.service..*.*(..)) && !@annotation(com.luckyvicky.woosan.global.annotation.SlaveDBRequest)")
    public void setWriteDataSourceType() {
        DataSourceContextHolder.setDataSourceType("masterDataSource");
    }
}

```


