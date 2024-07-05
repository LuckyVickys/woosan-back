package com.luckyvicky.woosan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.domain.board.service.PapagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PapagoServiceImpl implements PapagoService {

    @Value("${ncp.papago.clientId}")
    private String clientId;

    @Value("${ncp.papago.secretkey}")
    private String clientSecret;

    private static final String API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

    private static final Pattern KOREAN_PATTERN = Pattern.compile("[\\uAC00-\\uD7AF]");
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("[A-Za-z]");

    private String translate(String textToTranslate, String sourceLang, String targetLang) throws IOException {
        String text = URLEncoder.encode(textToTranslate, StandardCharsets.UTF_8.toString());
        URL url = new URL(API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
        con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

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

    @Override
    public BoardDTO tanslateBoardDetailPage(BoardDTO boardDTO) throws IOException {
        String detectedLanguage = LanguageDiscrimination(boardDTO.getContent());

        if(detectedLanguage.equals("ko")) {
            boardDTO.setTitle(translate(boardDTO.getTitle(), detectedLanguage, "en"));
            boardDTO.setContent(translate(boardDTO.getContent(), detectedLanguage, "en"));
        }else{
            boardDTO.setTitle(translate(boardDTO.getTitle(), detectedLanguage, "ko"));
            boardDTO.setContent(translate(boardDTO.getContent(), detectedLanguage, "ko"));
        }

        return boardDTO;
    }

}
