package com.matching.project.config;

import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenService jwtTokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorization = ((HttpServletRequest)request).getHeader("Authorization");
        // Authorized Bearer Token
        if (authorization != null)
        {
            if (Pattern.matches("^Bearer .*", authorization)) {
                String token = authorization.replaceAll("^Bearer( )*", "");
                //블랙리스트에 담겨진 access token 은 접근할 수 없다.
                if (jwtTokenService.hasBlackListKey(token)) {
                    request.setAttribute("exception", new CustomException(ErrorCode.DESTROY_JWT_TOKEN_EXCEPTION));
                }
                else {
                    //토근의 존재 유무, 토큰 유효성 체크, 토큰 유효기간 체크
                    CustomException cs = jwtTokenService.verifyToken(token);
                    if (cs == null) {
                        Authentication auth = jwtTokenService.getAuthentication(token);
                        User user = (User)auth.getPrincipal();
                        if (user.isWithdrawal()) {
                            log.error("{} : Withdrawal User", user.getEmail());
                            request.setAttribute("exception", new CustomException(ErrorCode.WITHDRAWAL_EXCEPTION));
                        } else if (user.isBlock()) {
                            log.error("{} : Blocked User", user.getEmail());
                            request.setAttribute("exception", new CustomException(ErrorCode.BLOCKED_EXCEPTION));
                        } else {
                            // refresh 토큰은 인증용으로 사용할 수 없음. 오로지 발급 용도로만 사용 가능함.
                            if (token.equals(jwtTokenService.getRefreshToken(jwtTokenService.getUserEmail(token)))) {
                                request.setAttribute("exception", new CustomException(ErrorCode.CANNOT_USE_REFRESH_TOKEN_FOR_AUTHENTICATION));
                            } else {
                                SecurityContextHolder.getContext().setAuthentication(auth);
                                log.error("{} : Valid Jwt Token", user.getEmail());
                            }
                        }
                    } else {
                        request.setAttribute("exception", cs);
                    }
                }

            }
        }
        chain.doFilter(request, response);
    }
}