# Board 기능

## Overview
사용자가 다양한 콘텐츠를 게시하고, 공유합니다. 게시물 작성, 조회, 수정, 삭제뿐만 아니라, 게시물의 번역 및 요약, 그리고 Elasticsearch 기반의 검색 기능과 연관 게시물 추천 기능을 포함하여 사용자 경험을 향상시켰습니다. 또한, 인기 검색어 순위를 제공하고 검색어 자동완성 기능을 통해 검색 편의성을 극대화합니다.

## Structure

- **BoardController**: 게시물 작성, 조회, 수정, 삭제 및 번역, 요약과 같은 다양한 기능을 처리하는 API 엔드포인트를 제공합니다.
- **ElasticsearchBoardController**: Elasticsearch 기반의 검색 및 자동완성 기능을 위한 API 엔드포인트를 제공합니다.
- **BoardService**: 게시물 작성, 조회, 수정, 삭제와 관련된 비즈니스 로직을 처리합니다.
- **ElasticsearchBoardService**: Elasticsearch를 사용하여 게시물 검색, 자동완성, 인기 검색어 순위 조회와 같은 기능을 처리합니다.
- **AIService**: 게시물의 번역 및 요약 기능을 처리하는 서비스로, Naver Papago 및 Clova API를 활용합니다.
- **BoardRepository**: JPA를 사용하여 게시물 데이터를 데이터베이스에 저장하고 조회하는 역할을 합니다.
- **ElasticsearchBoardRepository**: Elasticsearch에 저장된 게시물 데이터를 조회하는 역할을 합니다.

#### Features

1. <a href="https://github.com/LuckyVickys/woosan-back/blob/main/Readme.assets/board/Papago.md">**게시물 번역**</a>
   - **기능 설명**: 사용자가 게시물의 내용을 한국어에서 영어로, 또는 영어에서 한국어로 번역할 수 있는 기능을 제공합니다. 번역은 Naver Papago API를 사용하여 수행됩니다.
     - 입력된 게시물 내용을 바탕으로 자동으로 언어를 감지하고, 감지된 언어에 따라 번역을 수행합니다.
     
     ```java      
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
     ```

2. <a href="https://github.com/LuckyVickys/woosan-back/blob/main/Readme.assets/board/ClovaSummary.md">**게시물 요약**</a>
   - **기능 설명**: 한국어로 작성된 긴 게시물 내용을 3줄로 요약하여 사용자에게 제공하는 기능입니다. 이 기능은 Naver Clova AI를 활용하여 게시물의 중요한 내용을 추출합니다.
   - **핵심 메서드**: `summaryBoardDetailPage(BoardApiDTO boardApiDTO)`
     - 게시물의 제목과 내용을 Clova AI에 전달하여 요약된 텍스트를 반환받습니다.
     
     ```java
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
     ```

3. **Elasticsearch 기반 검색**
   - **기능 설명**: Elasticsearch를 활용하여 게시물을 검색하는 기능을 제공합니다. 사용자는 특정 키워드로 전체 또는 특정 카테고리 내에서 게시물을 검색할 수 있으며, 유사어를 활용한 검색도 가능합니다.
     - 검색 키워드와 일치하는 기본 검색과 검색 키워드와 관련된 게시글 게시글을 찾는 연관 검색을 모두 제공해 보다 포괄적인 검색 결과를 제공합니다.
     - 기본 검색 결과에서 중복된 검색 결과를 필터링하여 사용자에게 가장 관련성 높은 결과만 제공합니다.
     
     ```java
     public SearchPageResponseDTO searchWithStandardAndSynonyms(PageRequestDTO standardPageRequest, PageRequestDTO synonymPageRequest, String categoryName, String filterType, String keyword) {
         PageResponseDTO<SearchDTO> standardResult = searchByCategoryAndFilter(standardPageRequest, categoryName, filterType, keyword);
         PageResponseDTO<SearchDTO> synonymResult = searchWithSynonyms(synonymPageRequest, keyword);

         Set<Long> standardResultIds = standardResult.getDtoList().stream()
                 .map(SearchDTO::getId)
                 .collect(Collectors.toSet());

         List<SearchDTO> filteredSynonymResult = synonymResult.getDtoList().stream()
                 .filter(dto -> !standardResultIds.contains(dto.getId()))
                 .collect(Collectors.toList());

         PageImpl<SearchDTO> filteredSynonymResultPage = new PageImpl<>(
                 filteredSynonymResult,
                 PageRequest.of(synonymPageRequest.getPage() - 1, synonymPageRequest.getSize()),
                 synonymResult.getTotalCount() - standardResultIds.size()
         );

         PageResponseDTO<SearchDTO> filteredSynonymResultDTO = commonUtils.createPageResponseDTO(
                 synonymPageRequest, filteredSynonymResultPage.getContent(), filteredSynonymResultPage.getTotalElements()
         );

         return SearchPageResponseDTO.builder()
                 .StandardResult(standardResult)
                 .SynonymResult(filteredSynonymResultDTO)
                 .build();
     }
     ```

4. **검색 키워드 자동완성**
   - **기능 설명**: 사용자가 검색창에 키워드를 입력할 때, 입력한 내용에 기반하여 실시간으로 검색어를 추천합니다.
     - 사용자가 입력한 키워드를 바탕으로 실시간 검색어 자동완성을 제공합니다.
     - 뿐만 아니라, 전체 키워드를 입력할 필요 없이 초성 입력만으로도 자동완성을 제공해 사용자 편의성을 높혔습니다.
     
     ```java
     public List<String> autocomplete(String categoryName, String filterType, String keyword) {
         List<Board> result;
         String[] keywords = keyword.split("\\s+");
         String shouldQuery = generateShouldQuery(keywords, filterType);

         if (categoryName.equals("전체")) {
             switch (filterType) {
                 case "title":
                     result = elasticsearchBoardRepository.findByTitleOrKoreanTitleContainingAndCategoryNameNot(shouldQuery);
                     return filterResults(result, keywords);
                 case "content":
                     result = elasticsearchBoardRepository.findByContentOrKoreanContentContainingAndCategoryNameNot(shouldQuery);
                     return filterResults(result, keywords);
                 case "writer":
                     result = elasticsearchBoardRepository.autocompleteWriter(shouldQuery);
                     return filterResults(result, keywords);
                 default:
                     return List.of(); // 빈 리스트 반환
             }
         } else {
             switch (filterType) {
                 case "title":
                     result = elasticsearchBoardRepository.findByTitleContainingOrKoreanTitleContainingAndCategoryNameEquals(shouldQuery, categoryName);
                     return filterResults(result, keywords);
                 case "content":
                     result = elasticsearchBoardRepository.findByContentContainingOrKoreanContentContainingAndCategoryNameEquals(shouldQuery, categoryName);
                     return filterResults(result, keywords);
                 case "writer":
                     result = elasticsearchBoardRepository.autocompleteWriterAndCategoryName(shouldQuery, categoryName);
                     return filterResults(result, keywords);
                 default:
                     return List.of(); // 빈 리스트 반환
             }
         }
     }
     ```

5. **검색 순위 조회**
   - **기능 설명**: 사용자가 검색한 키워드를 바탕으로 인기 검색어 순위를 제공합니다. 순위 변동을 분석하여 상승, 하락 여부를 표시합니다.
     - 특정 시간대 동안의 인기 검색어를 집계하고, 검색어 순위 변동을 계산하여 제공합니다.
     - 검색어 순위와 함께 순위 변동을 표시하여 사용자가 검색 트렌드를 쉽게 파악할 수 있도록 합니다.
     
     ```java
     public List<RankingDTO> getRankingChanges() {
         List<String> currentRankings = getRanking("now-6h/h", "now"); // 현재 순위 가져오기
         List<String> pastRankings = getRanking("now-24h/h", "now-1h/h");  // 이전 순위 가져오기

         List<RankingDTO> changes = new ArrayList<>();
         Set<String> processedKeywords = new HashSet<>(); // 이미 처리된 키워드를 추적

         for (int i = 0; i < currentRankings.size(); i++) {
             String currentKeyword = currentRankings.get(i);
             int pastIndex = pastRankings.indexOf(currentKeyword);
             String symbol = (pastIndex == -1 || i < pastIndex) ? "+" : (i > pastIndex) ? "-" : "_";

             changes.add(new RankingDTO(i + 1, currentKeyword, symbol));
             processedKeywords.add(currentKeyword);
         }

         int rankCounter = currentRankings.size() + 1;
         for (int i = 0; i < pastRankings.size(); i++) {
             String pastKeyword = pastRankings.get(i);
             if (!processedKeywords.contains(pastKeyword)) {
                 changes.add(new RankingDTO(rankCounter++, pastKeyword, "-"));
             }
         }

         if (changes.size() > 10) {
             changes = changes.subList(0, 10);
         }

         return changes;
     }
     ```

6. **주간 조회수 기준 인기 게시물 조회**
   - **기능 설명**: 주간 조회수 기준으로 상위 7개의 인기 게시물을 조회합니다. 
     - 최근 7일간의 데이터를 바탕으로 조회수 상위 7개의 게시물을 검색하여 반환합니다.
     
     ```java
     public List<DailyBestBoardDTO> getTop5BoardsByViews() {
         ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
         ZonedDateTime startOfDay = now.minusDays(7).toLocalDate().atStartOfDay(ZoneId.of("UTC"));
         ZonedDateTime endOfDay = now.toLocalDate().atStartOfDay(ZoneId.of("UTC")).plusDays(1);

         Query searchQuery = buildTop5BoardsSearchQuery(startOfDay, endOfDay);
         SearchHits<Board> searchHits = executeTop5BoardsSearch(searchQuery);

         List<DailyBestBoardDTO> result = mapToDailyBestBoardDTOs(searchHits);
         log.info("Search Results: " + result);

         return result;
     }
     ```

7. **연관 게시물 추천**
   - **기능 설명**: 사용자가 현재 보고 있는 게시물과 관련된 게시물을 추천합니다. 이는 Elasticsearch의 유사 검색 기능을 활용하여 사용자가 관심을 가질 만한 추가 콘텐츠를 제안하는 데 사용됩니다.
     - 현재 게시물의 제목과 내용을 바탕으로 유사한 게시물을 검색하여 추천합니다. 중복되는 게시물은 필터링하고, 검색된 결과에서 랜덤으로 4개의 게시물을 반환합니다.
     
     ```java
     public List<SuggestedBoardDTO> getSuggestedBoards(Long currentBoardId, String title, String content) {
         Query searchQuery = buildSuggestedBoardSearchQuery(title, content);
         SearchHits<Board> searchHits = executeSuggestedBoardSearch(searchQuery);

         return filterAndMapSuggestedBoards(searchHits, currentBoardId, 4);
     }

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
