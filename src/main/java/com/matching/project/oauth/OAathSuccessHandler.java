package com.matching.project.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.entity.User;
import com.matching.project.repository.UserRepository;
import com.matching.project.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAathSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenService jwtTokenService;

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


        TokenDto tokenDto = TokenDto.builder()
                .email(email)
                .build();

        log.info("OAuth JWT Token Create");
        String token = jwtTokenService.createToken(tokenDto);
        log.info("{}", token);

        // api 리다이렉트에는 로그인 인증 후 jwt 토큰을 보낼 URI(react)가 설정되어야함.
        // 해당부분은 추후 프론트엔드 담당자와 상의가 필요함
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/auth/success?token="+ token);
//        String targetUrl;
//        targetUrl = UriComponentsBuilder.fromUriString("/common/login/auth/success")
//                .queryParam("token=" + token )
//                .build().toUriString();
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }



}