# NCP ELK
## NCP

### 1. Search Engine 클러스터 생성
- NCP(Naver Cloud Platform) 콘솔을 통해 Search Engine 클러스터를 생성합니다.
- target group을 생성하고 네트워크 로드밸런서를 생성한 뒤, 리스너를 추가해 클러스터에 연결합니다. 

### 2. Logstash 활용한 Cloud Database(MySQL) 연동
- Logstash를 설치합니다.
- logstash.conf를 작성합니다.


### logstash.conf
Logstash를 사용하여 MySQL 데이터베이스에서 데이터를 가져오고, 이를 Elasticsearch로 전송합니다. 아래의 logstash.conf 파일은 JDBC 입력 플러그인을 사용해 MySQL에서 데이터를 가져오고, 필터링 및 변환 작업을 거친 후 Elasticsearch에 적재하는 과정을 보여줍니다.

1. **input**: MySQL 데이터베이스에서 데이터를 추출하기 위한 JDBC 입력 설정을 정의합니다. 데이터베이스 연결 정보, SQL 쿼리, 페이징 처리 등이 포함됩니다.
2. **fillter**: 데이터를 변환하고 추가적인 필드를 생성합니다. 예를 들어, 한글 초성을 추출하는 Ruby 코드를 포함하여 검색어 필드를 생성합니다.
3. **filter**: 변환된 데이터를 Elasticsearch에 저장합니다. 각 데이터는 고유한 document_id로 관리되며, 필요시 업데이트 또는 삭제가 가능합니다.

Logstash 설정은 주기적으로 MySQL 데이터를 Elasticsearch로 동기화하고, 실시간으로 데이터 검색 및 분석이 가능하도록 합니다.


```bash
input {
  jdbc {
    jdbc_driver_class => "com.mysql.cj.jdbc.Driver"
    jdbc_driver_library => "/etc/logstash/plugin/mysql-connector-java-8.0.30.jar"
    jdbc_connection_string => "jdbc:mysql://mysql-url:3306/woosan?useSSL=false&allowPublicKeyRetrieval=true"
    jdbc_user => "*****************"
    jdbc_password => "*****************"
    jdbc_paging_enabled => true
    jdbc_page_size => 50
    statement => "
      SELECT b.*, m.nickname AS nickname, UNIX_TIMESTAMP(b.update_time) AS unix_ts_in_secs
      FROM board b
      JOIN member m ON b.writer_id = m.id
      WHERE UNIX_TIMESTAMP(b.update_time) > :sql_last_value
      ORDER BY b.update_time ASC
    "
    record_last_run => false
    clean_run => true
    tracking_column_type => "numeric"
    tracking_column => "unix_ts_in_secs"
    use_column_value => true
    last_run_metadata_path => "/etc/logstash/data/woosan"
    schedule => "*/5 * * * * *"
  }
}

filter {
  mutate {
    remove_field => ["@version", "host"]
    add_field => {
      "[@metadata][_id]" => "%{id}"
      "search_keyword" => ""  # search_keyword 필드를 빈 값으로 추가
    }
    rename => {
      "title" => "title"
      "content" => "content"
      "reg_date" => "reg_date"
      "update_time" => "update_time"
      "views" => "views"
      "likes_count" => "likes_count"
      "category_name" => "category_name"
      "reply_count" => "reply_count"
      "is_deleted" => "is_deleted"
      "nickname" => "nickname"
    }
    copy => {
      "title" => "synonym_title"
      "content" => "synonym_content"
    }
  }

  ruby {
    code => '
      def to_chosung(text)
        cho = ["ㄱ","ㄲ","ㄴ","ㄷ","ㄸ","ㄹ","ㅁ","ㅂ","ㅃ","ㅅ","ㅆ","ㅇ","ㅈ","ㅉ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"]
        result = ""
        text.each_char do |char|
          if char.ord >= 0xAC00 && char.ord <= 0xD7A3
            base = char.ord - 0xAC00
            cho_index = base / 28 / 21
            result += cho[cho_index]
          else
            result += char
          end
        end
        return result
      end

      event.set("korean_title", to_chosung(event.get("title")))
      event.set("korean_content", to_chosung(event.get("content")))
    '
  }
}

output {
  if [@metadata][_id] {
    if [is_deleted] {
      elasticsearch {
        hosts => ["http://elasticsearch-url:9200"]
        index => "board"
        document_id => "%{id}"
        action => "delete"
      }
    } else {
      elasticsearch {
        hosts => ["http://elasticsearch-url:9200"]
        index => "board"
        document_id => "%{id}"
        action => "update"
        doc_as_upsert => true
      }
    }
  }
  stdout { codec => rubydebug }
}

input {
  http {
    port => 5044
  }
}

filter {
  if [path] =~ "search" {
    grok {
      match => { "message" => "/search\\?q=%{WORD:search_query}" }
    }
    mutate {
      add_field => { "keyword" => "%{search_query}" }
      add_field => { "timestamp" => "%{@timestamp}" }
    }
  }
}

output {
  if [path] =~ "search" {
    elasticsearch {
      hosts => ["http://elasticsearch-url:9200"]
      index => "search_keywords"
    }
  }
  stdout { codec => rubydebug }
}
```

## Java를 활용한 Spring Data Elasticsearch
### 1. Gradle 의존성 추가
```java
// Elasticsearch dependencies
	implementation 'com.internetitem:logback-elasticsearch-appender:1.6'
	implementation 'dev.akkinoc.spring.boot:logback-access-spring-boot-starter:3.2.1'
	implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'
	implementation 'org.elasticsearch.client:elasticsearch-rest-high-level-client:7.10.0'
	implementation 'org.springframework.data:spring-data-elasticsearch:4.1.6'
	implementation 'org.apache.httpcomponents:httpclient:4.5.13'
```

### 2. properties 설정 (
클러스터와 연결된 로드밸런서의 주소를 application.properties 파일에 추가합니다:

```java
# Elasticsearch
spring.elasticsearch.uris=elasticsearch-url:9200
```

### 3. Config 설정
1. Elasticsearch와의 연결을 위한 설정 클래스를 작성합니다:

```java
package com.luckyvicky.woosan.global.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUris;

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(elasticsearchUris)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}

```

2. WoosanApplication에 Elasticsearch Repository를 명시합니다. 
```java
package com.luckyvicky.woosan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"com.luckyvicky.woosan.domain.board.repository.jpa",
		"com.luckyvicky.woosan.domain.member.repository.jpa",
		"com.luckyvicky.woosan.domain.likes.repository",
		"com.luckyvicky.woosan.domain.report.repository",
		"com.luckyvicky.woosan.domain.messages.repository",
		"com.luckyvicky.woosan.domain.fileImg.repository",
		"com.luckyvicky.woosan.domain.matching.repository"
})
@EnableElasticsearchRepositories(basePackages = "com.luckyvicky.woosan.domain.board.repository.elasticsearch")
@EnableRedisRepositories(basePackages = {
		"com.luckyvicky.woosan.domain.member.repository.redis",
		"com.luckyvicky.woosan.global.auth.repository"
})
@MapperScan(basePackages = {
		"com.luckyvicky.woosan.domain.board.mapper",
		"com.luckyvicky.woosan.domain.likes.mapper",
		"com.luckyvicky.woosan.domain.member.mybatisMapper"
})
public class WoosanApplication {

	public static void main(String[] args) {
		SpringApplication.run(WoosanApplication.class, args);
	}
}

```

### 4. Entity 수정 & Service 의존성 주입
1. 기존 Entity에 어노테이션을 추가하고, elasticsearch에 필요한 필드를  @Transient와 함께 명시합니다.
```java

@Entity
@Document(indexName = "board")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board {

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writerId", nullable = false)
    private Member writer;

    @Transient
    @Field(type = FieldType.Text, name = "nickname")
    private String nickname;

    @Column(nullable = false, length = 40)
    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Transient
    @Field(type = FieldType.Text, name = "korean_title")
    private String koreanTitle;

    @Transient
    @Field(type = FieldType.Text, name = "synonym_title")
    private String synonymTitle;


    @Column(nullable = false, length = 1960)
    @Field(type = FieldType.Text, name = "content")
    private String content;

    @Transient
    @Field(type = FieldType.Text, name = "korean_content")
    private String koreanContent;

    @Transient
    @Field(type = FieldType.Text, name = "synomym_content")
    private String synonymContent;

    @UpdateTimestamp
    @Column(nullable = false)
    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "update_time")
    private LocalDateTime updateTime;

    ...  
```
```java
@Data
@Document(indexName = "search_keywords")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchKeyword {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String keyword;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime timestamp;

}

```

3. elasticsearch 서비스를 위한 의존성을 주입합니다:

```java

@Log4j2
@Service
@RequiredArgsConstructor
public class ElasticsearchBoardServiceImpl implements ElasticsearchBoardService {

    private final ElasticsearchBoardRepository elasticsearchBoardRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final SearchKeywordRepository searchKeywordRepository;
    private final CommonUtils commonUtils;

}
```

### 4. elasticsearch 함수
기본/연관 검색, 기본/초성 검색어 자동 완성, 일별 검색어 순위, 연관 게시물, 주간 인기 개시물을 위한 elasticsearch 함수를 활용합니다:

ex) 연관 게시물 조회 
```java
 /**
     * 연관 게시물 4개 조회
     */
    @Override
    public List<SuggestedBoardDTO> getSuggestedBoards(Long currentBoardId, String title, String content) {
        Query searchQuery = buildSuggestedBoardSearchQuery(title, content);
        SearchHits<Board> searchHits = executeSuggestedBoardSearch(searchQuery);

        return filterAndMapSuggestedBoards(searchHits, currentBoardId, 4);
    }

    /**
     * 연관 게시물 검색 쿼리 생성
     */
    private Query buildSuggestedBoardSearchQuery(String title, String content) {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.multiMatchQuery(title, "synonym_title")
                                .analyzer("synonym_ngram_analyzer")
                                .boost(2.0f)) // 제목에 가중치 2
                        .should(QueryBuilders.multiMatchQuery(content, "synonym_content")
                                .analyzer("synonym_ngram_analyzer")
                                .boost(1.0f))) // 내용에 가중치 1
                .withPageable(PageRequest.of(0, 10)) // 일단 8개를 가져오고 나중에 필터링
                .build();
    }

    /**
     * 연관 게시물 검색 쿼리 실행
     */
    private SearchHits<Board> executeSuggestedBoardSearch(Query searchQuery) {
        return elasticsearchRestTemplate.search(searchQuery, Board.class);
    }

    /**
     * 검색 결과에서 현재 게시물 제외 및 DTO 매핑
     */
    private List<SuggestedBoardDTO> filterAndMapSuggestedBoards(SearchHits<Board> searchHits, Long currentBoardId, int limit) {
        List<Board> filteredBoards = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .filter(board -> !board.getId().equals(currentBoardId)) // 현재 게시물을 제외
                .collect(Collectors.toList());

        Collections.shuffle(filteredBoards); // 리스트를 섞어 랜덤 순서로 만듦

        return filteredBoards.stream()
                .limit(limit) // 필터링 후 지정된 개수만 반환
                .map(board -> commonUtils.mapObject(board, SuggestedBoardDTO.class))
                .collect(Collectors.toList());
    }
```
