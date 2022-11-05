package com.matching.project.oauth;

import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.token.TokenDto;
import com.matching.project.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenService jwtTokenService;

    @Value("${front.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth Attributes : {}", attributes);

        String email = null;
        if (attributes.containsKey("sub"))
            email = (String)attributes.get("email"); //Google
        else
            email = Integer.toString((Integer)attributes.get("id")); //Git


        // Jwt Token Create
        log.info("OAuth JWT Token Create");
        TokenClaimsDto tokenClaimsDto = TokenClaimsDto.builder()
                .email(email)
                .build();

        TokenDto tokens = jwtTokenService.createToken(tokenClaimsDto);

        // refresh Token save
        jwtTokenService.setRefreshToken(email, tokens.getRefresh());

        log.info("{}", tokens);

        // api 리다이렉트에는 로그인 인증 후 jwt 토큰을 보낼 URI(react)가 설정되어야함.
        getRedirectStrategy().sendRedirect(request, response, frontUrl + "/auth/success"
                + "?access=" + tokens.getAccess()
                + "&access_exp=" + tokens.getAccess_exp()
                + "&refresh=" + tokens.getRefresh()
        );
    }
}