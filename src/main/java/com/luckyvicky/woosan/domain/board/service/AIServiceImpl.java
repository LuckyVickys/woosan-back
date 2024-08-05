package com.luckyvicky.woosan.domain.board.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckyvicky.woosan.domain.board.dto.BoardApiDTO;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Log4j2
@Service
public class AIServiceImpl implements  AIService{

    @Value("${ncp.papago.clientId}")
    private String papagoClientId;

    @Value("${ncp.papago.secretkey}")
    private String papagoClientSecret;

    private static final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

    private static final Pattern KOREAN_PATTERN = Pattern.compile("[\\uAC00-\\uD7AF]");
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("[A-Za-z]");

    @Value("${ncp.clova.clientId}")
    private String clovaClientId;

    @Value("${ncp.clova.secretkey}")
    private String clovaClientSecret;

    private static final String CLOVA_API_URL = "https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize";

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
        }else{
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
