package com.matching.project.config;

import com.matching.project.oauth.CustomOAuth2UserService;
import com.matching.project.oauth.OAathSuccessHandler;
import com.matching.project.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .antMatchers("/v1/common/info")
                  .hasAnyRole("USER", "ADMIN")
//                     .antMatchers("/swagger-ui.html")
//                         .hasRole("ADMIN")
//                     .antMatchers("/*", "/v1/*", "/h2-console/*").permitAll();
                .antMatchers("/v1/project").hasRole("USER")
                .antMatchers("/v1/project/login/*").hasRole("USER")
                .antMatchers("/*").permitAll()
                //.anyRequest().authenticated();

                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .permitAll()

                .and()
                .oauth2Login()
                .loginPage("/v1/common/login/auth")
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