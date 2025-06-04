package nuts.tech_learning_archive.spring_security_playground.http_security;

import nuts.tech_learning_archive.spring_security_playground.http_security.custom.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthenticationProvider {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(new CustomAuthenticationProvider());

        return http.build();
    }

    // 스프링 빈으로 등록하게 되면 자동으로 필터 체인에 등록된다.
    @Bean
    AuthenticationProvider customAuthenticationProvider() {
        return new AuthenticationProvider();
    }
}