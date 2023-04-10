package me.springStudy.jwt.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * EnableWebSecurity : 기본적인 Web 보안을 활성화하겠다
     * 추가적인 설정 (아래 중 하나)
     *      - WebSecurityConfigurer implements
     *      - WebSecurityConfigurerAdapter extends
     * 이후 버전의 Spring Boot에서는 WebSecurityConfigurerAdapter를 사용할 수 없음
     * Bean 등록해서 사용해야 함
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()                                // HttpServletRequest 를 사용하는 요청들에 대한 접근 제한 설정
                .antMatchers("/api/hello").permitAll()   // 인증없이 접근을 허용
                .anyRequest().authenticated();                      // 나머지 요청들에 대해선 인증
    }
}
