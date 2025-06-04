package nuts.tech_learning_archive.spring_security_playground.http_security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Slf4j
public class FormLogin {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)// CSRF 보호 비활성화 (개발 중에만, 실제 환경에서는 활성화 필요)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll() // 로그인 페이지 및 정적 리소스 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .formLogin(form -> form
//                        .loginPage("/login") // 커스텀 로그인 페이지 경로
                                .defaultSuccessUrl("/home", true) // 로그인 성공 시 이동할 URL
                                .loginProcessingUrl("/login")
                                .successHandler((request, response, authentication) ->
                                        log.info("Authentication Success")
                                )
                                .failureUrl("/login?error")
                                .failureHandler((request, response, exception) ->
                                        log.error("Authentication Failure", exception)
                                )
                                .permitAll() // 로그인 페이지는 누구나 접근 가능
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL
                        .logoutSuccessUrl("/login?logout") // 로그아웃 성공 시 이동할 URL
                        .logoutSuccessHandler((request, response, authentication) ->
                                log.info("Logout Success")
                        )
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                );

        return http.build();
    }
}