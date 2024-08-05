package com.luckyvicky.woosan.global.auth.filter;

import com.luckyvicky.woosan.global.auth.dto.CustomUserInfoDTO;
import com.luckyvicky.woosan.global.auth.entity.RefreshToken;
import com.luckyvicky.woosan.global.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Log4j2
@Component
public class JWTUtil {

    private final Key key;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;
    private final RefreshTokenRepository refreshTokenRepository;

    public JWTUtil(
            @Value("${spring.jwt.secret}")String secretKey,
            @Value("${spring.jwt.access.expirationTime}") long accessTokenExpTime,
            @Value("${spring.jwt.refresh.expirationTime}") long refreshTokenExpTime,
            RefreshTokenRepository refreshTokenRepository
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Access Token 생성
     * @param member
     * @return Access Token String
     */
    public String createAccessToken(CustomUserInfoDTO member) {
        return createToken(member, accessTokenExpTime);
    }

    public String createRefreshToken(CustomUserInfoDTO member) {
        String refreshToken = createToken(member, refreshTokenExpTime);
        RefreshToken token = new RefreshToken(member.getId(), refreshToken);
        refreshTokenRepository.save(token);
        return refreshToken;
    }

    /**
     * JWT 생성
     * @param member
     * @param expireTime
     * @return JWT String
     */
    private String createToken(CustomUserInfoDTO member, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("id", member.getId());
        claims.put("email", member.getEmail());
        claims.put("nickname", member.getNickname());
        claims.put("isActive", member.getIsActive());
        claims.put("memberType", member.getMemberType());
        claims.put("socialType", member.getSocialType());
        claims.put("level", member.getLevel());
        claims.put("point", member.getPoint());
        claims.put("nextPoint", member.getNextPoint());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token에서 User ID 추출
     * @param token
     * @return User ID
     */
    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    /**
     * JWT 검증
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * RefreshToken 검증
     * @param refreshToken
     * @return IsValidate
     */
    public boolean validateRefreshToken(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findById(refreshToken);
        return token.isPresent() && validateToken(token.get().getRefreshToken());
    }

    public String getAccessTokenFromRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        CustomUserInfoDTO userInfo = new CustomUserInfoDTO(
                claims.get("id", Long.class),
                claims.get("email", String.class),
                claims.get("memberType", String.class),
                claims.get("level", String.class)
        );
        return createAccessToken(userInfo);
    }

    /**
     * JWT Claims 추출
     * @param accessToken
     * @return JWT Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
