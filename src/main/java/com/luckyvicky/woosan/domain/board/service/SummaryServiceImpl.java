//package com.luckyvicky.woosan.domain.board.service;
//
//import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
//import lombok.extern.log4j.Log4j2;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//@Log4j2
//@Service
//public class SummaryServiceImpl implements SummaryService {
//
//    @Value("${ncp.clova.clientId}")
//    private String clientId;
//
//    @Value("${ncp.clova.secretkey}")
//    private String clientSecret;
//
//    private static final String API_URL = "https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize";
//
//    @Override
//    public String summaryBoardDetailPage(BoardDTO boardDTO) throws IOException {
//        HttpURLConnection con = createConnection(API_URL, clientId, clientSecret);
//        JSONObject requestBody = createRequestBody(boardDTO);
//        sendRequest(con, requestBody);
//        return getResponse(con);
//    }
//
//    // Http 요청 설정
//    private HttpURLConnection createConnection(String apiUrl, String clientId, String clientSecret) throws IOException {
//        URL url = new URL(apiUrl);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("POST");
//        con.setRequestProperty("Content-Type", "application/json");
//        con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
//        con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
//
//        con.setDoOutput(true);  // 출력 스트림을 사용해 데이터를 전송할 수 있게 설정
//        return con;
//    }
//
//    // JSON 요청 본문 생성
//    private JSONObject createRequestBody(BoardDTO boardDTO) {
//        JSONObject document = new JSONObject();
//        document.put("title", boardDTO.getTitle());
//        document.put("content", boardDTO.getContent());
//
//        JSONObject option = new JSONObject();
//        option.put("language", "ko");
//        option.put("model", "general");
//        option.put("tone", 0);
//        option.put("summaryCount", 3);
//
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("document", document);
//        requestBody.put("option", option);
//        return requestBody;
//    }
//
//    // 요청 전송
//    private void sendRequest(HttpURLConnection con, JSONObject requestBody) throws IOException {
//        try (OutputStream os = con.getOutputStream()) {
//            byte[] input = requestBody.toString().getBytes("utf-8");
//            os.write(input, 0, input.length);
//        }
//    }
//
//    // 응답 처리
//    private String getResponse(HttpURLConnection con) throws IOException {
//        int responseCode = con.getResponseCode();
//        if (responseCode == 200) {
//            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
//                StringBuilder response = new StringBuilder();
//                String responseLine;
//                while ((responseLine = br.readLine()) != null) {
//                    response.append(responseLine.trim());
//                }
//                JSONObject responseJson = new JSONObject(response.toString());
//                return responseJson.getString("summary");
//            }
//        } else {
//            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"))) {
//                StringBuilder response = new StringBuilder();
//                String responseLine;
//                while ((responseLine = br.readLine()) != null) {
//                    response.append(responseLine.trim());
//                }
//                log.debug(response);
//                throw new IOException("Error: " + response.toString());
//            }
//        }
//    }
//}