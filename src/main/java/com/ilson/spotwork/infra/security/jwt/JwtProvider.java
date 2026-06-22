package com.ilson.spotwork.infra.security.jwt;

import com.ilson.spotwork.common.exception.CustomException;
import com.ilson.spotwork.common.exception.ErrorCode;
import com.ilson.spotwork.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey signingKey;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    public String generateAccessToken(Long userId, Role role) {
        log.info("[JWT] AccessToken 발급 시도");
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.name())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    //Refresh Token 생성
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // 토큰 검증
     public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("[JWT] 만료된 토큰: {}", e.getMessage());
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            log.warn("[JWT] 유효하지 않은 토큰: {}", e.getMessage());
            throw  new CustomException(ErrorCode.INVALID_TOKEN);
        }
     }

     // userId 추출
     public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
     }

     // role 추출
     public Role getRole (String token) {
        return Role.valueOf(getClaims(token).get("role", String.class));
     }

     // Claims 파싱
     private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
     }

    // Authentication 객체 생성
     public Authentication getAuthentication(String token) {
        Long userId = getUserId(token);
        Role role = getRole(token);
         UserDetails userDetails = User.builder()
                 .username(String.valueOf(userId))
                 .password("")
                 .roles(role.name())
                 .build();
         return new UsernamePasswordAuthenticationToken(
                 userDetails, null, userDetails.getAuthorities());

     }
}
