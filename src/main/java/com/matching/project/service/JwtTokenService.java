package com.matching.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.token.TokenDto;
import com.matching.project.dto.token.TokenReissueRequestDto;
import com.matching.project.dto.token.TokenReissueResponseDto;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenService {
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisService redisService;

    @Value("${key.jwt.secret}")
    private String secretKey;

    public void setSecretKeyForTest(String secretKey) {
        this.secretKey = secretKey;
    }

    // 토큰 유효시간 설정
    private long accessTokenPeriod = 1000L * 60L * 60L * 2L; // 2시간
    private long refreshTokenPeriod = 1000L * 60L * 60L * 24L * 7L; // 7일

    //private long accessTokenPeriod = 1000L * 15L; // 15초
    //private long refreshTokenPeriod = 1000L * 60L * 1L; // 1분

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64Utils.encodeToUrlSafeString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public TokenDto createToken(TokenClaimsDto tokenClaimsDto) {
        //Header
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        //Claims(=payload)
        Claims claims = Jwts.claims();
        claims.put("email", tokenClaimsDto.getEmail());

        Date now = new Date();
        return TokenDto.builder()
                .access(Jwts.builder().setHeader(headers)
                        .setClaims(claims)
                        .setSubject("user-auth")
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + accessTokenPeriod))
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .setId(UUID.randomUUID().toString())
                        .compact())
                .access_exp(Long.toString((now.getTime() + accessTokenPeriod)/1000L))
                .refresh(Jwts.builder().setHeader(headers)
                        .setClaims(claims)
                        .setSubject("user-auth")
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + refreshTokenPeriod))
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .setId(UUID.randomUUID().toString())
                        .compact())
                .build();
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
    public CustomException verifyToken(String token) {
        CustomException cs = null;
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            claims.getBody().getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            cs = new CustomException(ErrorCode.EXPIRED_JWT_ACCESS_TOKEN_EXCEPTION);
        } catch (Exception e) {
            cs = new CustomException(ErrorCode.NOT_VALID_JWT_TOKEN_EXCEPTION);
        }
        return cs;
    }

    // access 토큰 재발급
    public TokenReissueResponseDto accessTokenReissue(TokenReissueRequestDto dto) {
        // 기존 access 토큰이 블랙리스트에 있는지 확인
        if (!hasBlackListKey(dto.getAccess()))
            setBlackList(dto.getAccess());

        // refresh 토큰이 유효한지 확인
        CustomException cs = verifyToken(dto.getRefresh());
        String email = null;
        if (cs == null) {
            email = getUserEmail(dto.getRefresh());
            if (!hasRefreshToken(email)) {
                throw new CustomException(ErrorCode.NOT_FIND_JWT_REFRESH_TOKEN_EXCEPTION);
            }
        }
        else {
            if (cs.getErrorCode() == ErrorCode.EXPIRED_JWT_ACCESS_TOKEN_EXCEPTION) {
                throw new CustomException(ErrorCode.EXPIRED_JWT_REFRESH_TOKEN_EXCEPTION);
            } else {
                throw cs;
            }
        }
        // access 토큰 재발급
        TokenClaimsDto tokenClaimsDto = TokenClaimsDto.builder()
                .email(email)
                .build();
        TokenDto tokenDto = createToken(tokenClaimsDto);
        return TokenReissueResponseDto.builder()
                .access(tokenDto.getAccess())
                .access_exp(tokenDto.getAccess_exp())
                .build();
    }

    public void setRefreshToken(String email, String refreshToken){
        int timeout =  Math.toIntExact(refreshTokenPeriod) / (1000 * 60);
        redisService.set(email, refreshToken, timeout);
        log.info("{} : refresh token save", email);
    }

    public String getRefreshToken(String email) {
        return redisService.get(email, String.class);
    }

    public void deleteRefreshToken(String email) {
        redisService.delete(email);
    }

    public boolean hasRefreshToken(String email) {return redisService.hasKey(email); }

    public void setBlackList(String key){
        // access token의 수명을 넘기면 어짜피 접근하지 못하기때문에,
        // redis 조회간의 속도 향상을 위해 수명 설정
        // 원래라면 남은 시간을 계산해서 그 만큼만 설정해야하나, 해당 부분은 추후 수정하도록 하자
        int timeout =  Math.toIntExact(accessTokenPeriod) / (1000 * 60);
        redisService.set(key, "access_token", timeout);
    }

    public boolean hasBlackListKey(String key) {
        return redisService.hasKey(key);
    }

}
