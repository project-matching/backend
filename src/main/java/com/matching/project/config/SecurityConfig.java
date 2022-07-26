package com.matching.project.config;

import com.matching.project.oauth.CustomOAuth2UserService;
import com.matching.project.oauth.OAathSuccessHandler;
import com.matching.project.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenService jwtTokenService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAathSuccessHandler oAathSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable();
        http
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()

                //CommonController
                .antMatchers("/v1/common/logout").hasAnyRole("USER", "ADMIN")
                .antMatchers("/v1/common/**").anonymous()

                //UserController
                .antMatchers("/v1/user/list", "/v1/user/block/**", "/v1/user/unblock/**").hasAnyRole("USER", "ADMIN") // 테스트를 위해 유저권한도 일시 부여
                .antMatchers(HttpMethod.POST, "/v1/user").anonymous()
                .antMatchers(HttpMethod.GET,"/v1/user").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PATCH,"/v1/user").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE,"/v1/user").hasAnyRole("USER", "ADMIN")
                .antMatchers("/v1/user/info", "/v1/user/password").hasAnyRole("USER", "ADMIN")

                //ProjectController
                //.antMatchers("/v1/project/recruitment/*").hasAnyRole("USER", "ADMIN", "ANONYMOUS")
                //.antMatchers("/v1/project/recruitment/complete/*").hasAnyRole("USER", "ADMIN", "ANONYMOUS")

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
                .successHandler(oAathSuccessHandler)
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