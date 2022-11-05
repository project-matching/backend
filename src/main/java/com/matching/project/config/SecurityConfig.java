package com.matching.project.config;

import com.matching.project.error.CustomAccessDeniedHandler;
import com.matching.project.error.CustomAuthenticationEntryPoint;
import com.matching.project.oauth.CustomOAuth2UserService;
import com.matching.project.oauth.OAuthFailHandler;
import com.matching.project.oauth.OAuthSuccessHandler;
import com.matching.project.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenService jwtTokenService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final OAuthFailHandler oAuthFailHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER > ROLE_ANONYMOUS");
        return roleHierarchy;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://ec2-3-37-7-111.ap-northeast-2.compute.amazonaws.com:3000"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin" ,"AccessControlAllowOrigin"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable();
        http
                .httpBasic().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService),
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                // 인증 에러 핸들링
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                // 인가 에러 핸들링
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()

                .authorizeRequests()

                //ProjectController
                .antMatchers(HttpMethod.POST, "/v1/project").hasRole("USER")
                .antMatchers(HttpMethod.GET,"/v1/project/create").hasRole("USER")
                .antMatchers(HttpMethod.GET,"/v1/project/recruitment/**")
                    .hasRole("ANONYMOUS")
                .antMatchers(HttpMethod.GET, "/v1/project/participate")
                .hasRole("USER")
                .antMatchers(HttpMethod.GET, "/v1/project/application")
                .hasRole("USER")
                .antMatchers(HttpMethod.GET, "/v1/project/create/self")
                .hasRole("USER")
                .antMatchers(HttpMethod.GET, "/v1/project/**/update")
                .hasRole("USER")
                .antMatchers(HttpMethod.GET, "/v1/project/create")
                .hasRole("USER")
                .antMatchers(HttpMethod.GET, "/v1/project/*")
                .hasRole("ANONYMOUS")
                .antMatchers(HttpMethod.PATCH,"/v1/project/*")
                .hasRole("USER")
                .antMatchers(HttpMethod.DELETE,"/v1/project/*")
                .hasRole("USER")

                //CommonController
                .antMatchers("/v1/common/logout").hasRole("USER")
                .antMatchers("/v1/common/**").anonymous()

                //UserController
                .antMatchers("/v1/user/list", "/v1/user/block/**", "/v1/user/unblock/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/v1/user").anonymous()
                .antMatchers(HttpMethod.GET,"/v1/user").hasRole("USER")
                .antMatchers(HttpMethod.PATCH,"/v1/user").hasRole("USER")
                .antMatchers(HttpMethod.DELETE,"/v1/user").hasRole("USER")
                .antMatchers("/v1/user/info", "/v1/user/password").hasRole("USER")

                //CommentController
                .antMatchers(HttpMethod.POST,"/v1/comment/**").hasRole("USER")
                .antMatchers(HttpMethod.GET,"/v1/comment/**").hasRole("ANONYMOUS")
                .antMatchers(HttpMethod.PATCH,"/v1/comment/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE,"/v1/comment/**").hasRole("USER")

                //PositionController
                .antMatchers(HttpMethod.POST,"/v1/position").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/v1/position").hasRole("USER")
                .antMatchers(HttpMethod.PUT,"/v1/position/**").hasRole("ADMIN")

                //NotificationController
                .antMatchers(HttpMethod.POST,"/v1/notification").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/v1/notification").hasRole("USER")
                .antMatchers(HttpMethod.GET,"/v1/notification/**").hasRole("USER")

                //ProjectParticipateController
                .antMatchers("/v1/participate").hasRole("USER")
                .antMatchers("/v1/participate/*/permit").hasRole("USER")
                .antMatchers("/v1/participate/*/refusal").hasRole("USER")
                .antMatchers(HttpMethod.GET,"/v1/participate/*").hasRole("USER")

                //BookMarkController
                .antMatchers(HttpMethod.POST,"/v1/bookmark/*").hasRole("USER")
                .antMatchers(HttpMethod.DELETE,"/v1/bookmark/*").hasRole("USER")
                .antMatchers(HttpMethod.GET,"/v1/bookmark").hasRole("USER")

                //TechnicalStackController
                .antMatchers(HttpMethod.GET,"/v1/technicalStack").hasRole("USER")
                .antMatchers(HttpMethod.POST,"/v1/technicalStack").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/v1/technicalStack/*").hasRole("ADMIN")

                //ProjectPositionController
                .antMatchers(HttpMethod.DELETE, "/v1/projectposition/*/withdrawal").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/v1/projectposition/*/expulsion").hasRole("USER")

                //Token
                .antMatchers(HttpMethod.POST, "/v1/user/token/reissue").hasAnyRole("USER")

                //AnyRequest
                .anyRequest().permitAll()

                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .permitAll()

                .and()
                .oauth2Login()
                .authorizationEndpoint()
                    .baseUri("/v1/oauth2/authorization")
                .and()
                .loginPage("/")
                .successHandler(oAuthSuccessHandler)
                .failureHandler(oAuthFailHandler)
                .userInfoEndpoint()
                .userService(customOAuth2UserService);


        // Spring Security 와 h2-console 를 함께 쓰기위한 옵션
        http.
                headers()
                .addHeaderWriter(
                        new XFrameOptionsHeaderWriter(
                                new WhiteListedAllowFromStrategy(Arrays.asList("localhost"))    // 여기!
                        )
                )
                .frameOptions().sameOrigin();
    }
}