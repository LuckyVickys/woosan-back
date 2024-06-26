package com.luckyvicky.woosan.domain.board.service;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PapagoServiceImpl implements PapagoService {

    @Value("${ncp.papago.clientId}")
    private String clientId;

    @Value("${ncp.papago.secretkey}")
    private String clientSecret;

    private static final String API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

    @Value("${langdetect.profiles.path}")
    private String profilesPath;

    @PostConstruct
    public void init() throws LangDetectException {
        DetectorFactory.loadProfile(profilesPath);
    }

    @Override
    public BoardDTO tanslateBoardDetailPage(BoardDTO boardDTO) throws IOException, LangDetectException {
        String defineLanguage = stringDetector(boardDTO.getContent());

        if(defineLanguage.equals("ko")) {
            boardDTO.setTitle(translate(boardDTO.getTitle(), defineLanguage, "en"));
            boardDTO.setContent(translate(boardDTO.getContent(), defineLanguage, "en"));
        }else{
            boardDTO.setTitle(translate(boardDTO.getTitle(), defineLanguage, "ko"));
            boardDTO.setContent(translate(boardDTO.getContent(), defineLanguage, "ko"));
        }

        return boardDTO;
    }

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

    public String stringDetector(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        String lang = detector.detect();
        return lang;
    }

}
