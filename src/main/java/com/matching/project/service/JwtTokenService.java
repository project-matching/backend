package com.matching.project.service;

import com.matching.project.dto.common.TokenDto;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenService {
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${key.jwt.secret}")
    private String secretKey;

    public void setSecretKeyForTest(String secretKey) {
        this.secretKey = secretKey;
    }

    // 토큰 유효시간 설정
    private long accessTokenPeriod = 1000L * 60L * 60L * 24L; // 1일

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64Utils.encodeToUrlSafeString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(TokenDto tokenDto) {
        //Header
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        //Claims(=payload)
        Claims claims = Jwts.claims();
        claims.put("email", tokenDto.getEmail());

        Date now = new Date();
        String jwtAccessToken = Jwts.builder().setHeader(headers)
                .setClaims(claims)
                .setSubject("user-auth")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return jwtAccessToken;
    }

    // 인증 성공시 SecurityContextHolder에 저장할 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 claims 추출
    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // 토큰에서 이메일 추출
    public String getUserEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("Jwt Token Expired");
            return false;
        } catch (Exception e) {
            log.warn("Jwt Token verify Error");
            return false;
        }
    }
}
