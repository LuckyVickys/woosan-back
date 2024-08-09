# Papago Translation Service

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

## II. 한영 번역 Service 구현
### AIServiceImpl
AI를 이용하는 서비스 클래스입니다.
Papago Translation 기능을 이용하여 게시글의 제목과 본문을 한국어와 영어로 번역합니다.

```java
@Log4j2
@Service
public class AIServiceImpl implements AIService {

    @Value("${ncp.papago.clientId}")
    private String papagoClientId;

    @Value("${ncp.papago.secretkey}")
    private String papagoClientSecret;

    private static final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

    private static final Pattern KOREAN_PATTERN = Pattern.compile("[\\uAC00-\\uD7AF]");
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("[A-Za-z]");

    /**
     * 본문 한영 번역
     *
     * @param boardApiDTO
     * @return boardApiDTO(번역된 게시글)
     * @throws IOException
     */
    public BoardApiDTO translateBoardDetailPage(BoardApiDTO boardApiDTO) throws IOException {
        String detectedLanguage = LanguageDiscrimination(boardApiDTO.getContent());

        if(detectedLanguage.equals("ko")) {
            boardApiDTO.setTitle(translate(boardApiDTO.getTitle(), detectedLanguage, "en"));
            boardApiDTO.setContent(translate(boardApiDTO.getContent(), detectedLanguage, "en"));
        } else {
            boardApiDTO.setTitle(translate(boardApiDTO.getTitle(), detectedLanguage, "ko"));
            boardApiDTO.setContent(translate(boardApiDTO.getContent(), detectedLanguage, "ko"));
        }
        return boardApiDTO;
    }

    private String translate(String textToTranslate, String sourceLang, String targetLang) throws IOException {
        String text = URLEncoder.encode(textToTranslate, StandardCharsets.UTF_8.toString());

        HttpURLConnection con = createConnection(PAPAGO_API_URL, papagoClientId, papagoClientSecret);

        String postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + text;
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(postParams);
            wr.flush();
        }

        int responseCode = con.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode == 200 ? con.getInputStream() : con.getErrorStream()))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.toString());
        return rootNode.path("message").path("result").path("translatedText").asText();
    }

    private String LanguageDiscrimination(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "unknown";
        }
        if (KOREAN_PATTERN.matcher(text).find()) {
            return "ko";
        }
        if (ENGLISH_PATTERN.matcher(text).find()) {
            return "en";
        }
        return "unknown";
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
}

```
## III. 한영 번역 Controller 구현
### BoardController
한영 번역 API 엔드포인트를 제공합니다.

```java
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final AIService aiService;

    @PostMapping("/{id}/translate")
    public ResponseEntity<BoardApiDTO> boardDetailTranslate(@PathVariable("id") Long id, @RequestBody BoardApiDTO boardApiDTO) {
        try {
            boardApiDTO = aiService.translateBoardDetailPage(boardApiDTO);
            return ResponseEntity.ok(boardApiDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

```
이 API는 주어진 게시글 내용을 한국어에서 영어로, 또는 영어에서 한국어로 번역하여 반환합니다. 클라이언트는 게시글 ID와 함께 BoardApiDTO 객체를 POST 요청으로 보내면, 번역된 결과를 응답으로 받습니다.
