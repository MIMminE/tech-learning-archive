package nuts.tech_learning_archive.spring_security_playground;

import jakarta.servlet.ServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class TestConfig {

    @Bean
    SecurityFilterChain springSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(ServletRequest::isSecure).hasRole("USER")
                        .requestMatchers(HttpMethod.POST).hasRole("ADMIN")
                        .anyRequest().authenticated()); // 애플리케이션의 모든 엔드포인트가 최소한 인증된 보안 컨텍스트가 있어야 함


        return http.build();
    }
}
