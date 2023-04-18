package me.springStudy.jwt.config;

import me.springStudy.jwt.jwt.JwtAccessDeniedHandler;
import me.springStudy.jwt.jwt.JwtAuthenticationEntryPoint;
import me.springStudy.jwt.jwt.JwtSecurityConfig;
import me.springStudy.jwt.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * EnableWebSecurity : 기본적인 Web 보안을 활성화하겠다
 * 추가적인 설정 (아래 중 하나)
 *      - WebSecurityConfigurer implements
 *      - WebSecurityConfigurerAdapter extends
 * 이후 버전의 Spring Boot 에서는 WebSecurityConfigurerAdapter 를 사용할 수 없음
 * Bean 등록해서 사용해야 함
 *
 * EnableGlobalMethodSecurity : PreAuthorize 어노테이션을 메소드 단위로 추가하기 위함 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
                            TokenProvider tokenProvider,
                            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    /**
     * 패스워드 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return  new BCryptPasswordEncoder();
    }


    /**
     * h2 Console, 파비콘은 Security 수행하지 않고 접근할 수 있도록 설정
     * h2 Console URL : http://localhost:{port}/h2-console
     * JDBC URL application.yml 쪽과 일치하는지 확인
     */
    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 토큰 방식을 사용하기 때문에 csrf disable
                .csrf().disable()

                // exceptionHandling 할 때, authenticationEntryPoint, accessDeniedHandler 를 해줄 클래스 추가(만들었던)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // h2 Console을 위한 설정
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션 관리를 설정
                // 서버 측에서 세션을 유지하지 않음
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()                                       // HttpServletRequest 를 사용하는 요청들에 대한 접근 제한 설정
                .antMatchers("/api/hello").permitAll()          // 인증없이 접근을 허용
                .antMatchers("/api/authenticate").permitAll()   // 토큰을 받기 위한 api
                .antMatchers("/api/signup").permitAll()
                .anyRequest().authenticated()                              // 나머지 요청들에 대해선 인증

                // addFilterBefore로 등록했던 JwtSecurityConfig 클래스 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
