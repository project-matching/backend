package com.matching.project.config;

import com.matching.project.oauth.CustomOAuth2UserService;
import com.matching.project.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
    private final CustomOAuth2UserService customOAuth2UserService;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable();
        http
                .httpBasic()
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
          
                .and()
                .authorizeRequests()
//                     .antMatchers("/v1/common/info")
//                         .hasAnyRole("USER", "ADMIN")
//                     .antMatchers("/swagger-ui.html")
//                         .hasRole("ADMIN")
//                     .antMatchers("/*", "/v1/*", "/h2-console/*").permitAll();
                .anyRequest().authenticated()
          
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .permitAll()

                .and()
                .oauth2Login()
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

        // 새로 구현한 Filter를 UsernamePasswordAuthenticationFilter layer에 삽입
        http.
                formLogin().
                disable().
                addFilterAt(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        // 테스트 용 인메모리 사용자 저장
        auth.inMemoryAuthentication()
                .withUser("user@naver.com")
                    .password(passwordEncoder().encode("user"))
                    .roles("USER")
                .and()
                .withUser("admin@naver.com")
                .password(passwordEncoder().encode("admin"))
                    .roles("USER","ADMIN");

    }

    protected CustomUsernamePasswordAuthenticationFilter getAuthenticationFilter() {
        CustomUsernamePasswordAuthenticationFilter authFilter = new CustomUsernamePasswordAuthenticationFilter();
        try {
            authFilter.setFilterProcessesUrl("/v1/common/login");
            authFilter.setAuthenticationManager(this.authenticationManagerBean());
            authFilter.setUsernameParameter("email");
            authFilter.setPasswordParameter("password");
            authFilter.setAuthenticationSuccessHandler(new CustomSuccessHandler());
            authFilter.setAuthenticationFailureHandler(new CustomFailureHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authFilter;
    }
}