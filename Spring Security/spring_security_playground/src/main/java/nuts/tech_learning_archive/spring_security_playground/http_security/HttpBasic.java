package nuts.tech_learning_archive.spring_security_playground.http_security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Configuration
@EnableWebSecurity
public class HttpBasic {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(basic -> basic
                .realmName("security") // 인증 영역 이름 설정
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\"}"); // JSON 응답 반환
                })
        );
        return http.build(); // SecurityFilterChain 객체 반환
    }
}