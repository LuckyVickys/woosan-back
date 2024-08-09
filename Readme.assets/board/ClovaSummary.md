# Clova Summary 클로바 요약 기능
## I. DTO 구현
### 1. BoardApiDTO
외부 API로 전송할 게시글 정보를 포함한 DTO 클래스입니다.

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardApiDTO {
    private String title;
    private String content;
}
```

## II. 세 줄 요약 Service 구현
### AIServiceImpl
AI를 이용하는 서비스 클래스입니다. <br>
Clova Summary 기능을 이용하여 한국어로 작성된 본문을 본문 어투를 유지한 채 3줄로 요약합니다.

```java
@Log4j2
@Service
public class AIServiceImpl implements  AIService{
    @Value("${ncp.clova.clientId}")
    private String clovaClientId;

    @Value("${ncp.clova.secretkey}")
    private String clovaClientSecret;

    private static final String CLOVA_API_URL = "https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize";

    /**
     * 본문 요약
     * @param boardApiDTO
     * @return summary(요약된 글)
     * @throws IOException
     */
    @Override
    public String summaryBoardDetailPage(BoardApiDTO boardApiDTO) throws IOException {
        HttpURLConnection con = createConnection(CLOVA_API_URL, clovaClientId, clovaClientSecret);
        con.setRequestProperty("Content-Type", "application/json");
        JSONObject requestBody = createRequestBody(boardApiDTO);
        sendRequest(con, requestBody);
        return getResponse(con);
    }

    // Http 요청 설정
    private HttpURLConnection createConnection(String apiUrl, String clientId, String clientSecret) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
        con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

        con.setDoOutput(true);  // 출력 스트림을 사용해 데이터를 전송할 수 있게 설정
        return con;
    }

    // 요약 JSON 요청 본문 생성
    private JSONObject createRequestBody(BoardApiDTO boardApiDTO) {
        JSONObject document = new JSONObject();
        document.put("title", boardApiDTO.getTitle());
        document.put("content", boardApiDTO.getContent());

        JSONObject option = new JSONObject();
        option.put("language", "ko");
        option.put("model", "general");
        option.put("tone", 0);
        option.put("summaryCount", 3);

        JSONObject requestBody = new JSONObject();
        requestBody.put("document", document);
        requestBody.put("option", option);
        return requestBody;
    }

    // 요약 요청 전송
    private void sendRequest(HttpURLConnection con, JSONObject requestBody) throws IOException {
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    // 요약 응답 처리
    private String getResponse(HttpURLConnection con) throws IOException {
        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject responseJson = new JSONObject(response.toString());
                return responseJson.getString("summary");
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                log.debug(response);
                throw new IOException("Error: " + response.toString());
            }
        }
    }
}
```

## III. 세 줄 요약 Controller 구현
### BoardController
세 줄 요약 API 엔드포인트를 제공합니다.

```java
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final AIService aiService;

    @PostMapping("/{id}/summary")
    public ResponseEntity<String> boardDetailSummary(@PathVariable("id") Long id, @RequestBody BoardApiDTO boardApiDTO) {
        String summary = "";
        try {
            summary = aiService.summaryBoardDetailPage(boardApiDTO);
            System.out.println(summary);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```
