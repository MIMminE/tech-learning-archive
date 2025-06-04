package nuts.tech_learning_archive.spring_security_playground.http_security;

import nuts.tech_learning_archive.spring_security_playground.http_security.custom.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class UserDetailsService {

    // HttpSecurity 빌더 클래스를 통한 디테일 서비스 직접 등록하여 필터 체인 등록하는 방법
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.userDetailsService(new CustomUserDetailsService());

        return http.build();
    }

    // 디테일 서비스 구현체를 스프링 빈으로 등록함으로써 자동으로 필터 체인에 등록되게 하는 방법
    @Bean
    org.springframework.security.core.userdetails.UserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }
}
