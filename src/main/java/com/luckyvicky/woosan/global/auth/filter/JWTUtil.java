package com.luckyvicky.woosan.global.auth.filter;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {

    private String key;
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        this.key = secret;
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRoles(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("memberType", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String generateToken(Map<String, Object> valueMap, int min) {
        return Jwts.builder()
                .claims(valueMap)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + min))
                .signWith(secretKey)
                .compact();
    }

    public Map<String, Object> validateToken(String token) {

        Map<String, Object> claim = null;

        try {
            claim = Jwts.parser()
                    .setSigningKey(this.secretKey)
                    .build()
                    .parseSignedClaims(token)   // 검증, 실패 시 예외 발생
                    .getBody();

        } catch(MalformedJwtException malformedJwtException) {
            throw new JwtException("잘못된 토큰 형식입니다.");
        } catch (ExpiredJwtException expiredJwtException) {
            throw new JwtException("토큰이 만료되었습니다.");
        } catch (InvalidClaimException invalidClaimException) {
            throw new JwtException("클레임이 유효하지 않습니다.");
        } catch (JwtException jwtException) {
            throw new JwtException("JWT ERROR");
        } catch (Exception e) {
            throw new JwtException("ERROR");
        }

        return claim;
    }
}
