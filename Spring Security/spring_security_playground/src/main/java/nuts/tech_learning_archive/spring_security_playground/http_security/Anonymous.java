package nuts.tech_learning_archive.spring_security_playground.http_security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Anonymous {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.anonymous(anonymous -> anonymous
                .principal("guest") // 익명 사용자의 주체의 값을 guest로 설정한다. 기본값은 anonymousUser 이다.
                .authorities("ROLE_GUEST") // 익명 사용자에게 "ROLE_GUEST" 권한을 부여한다.
        );
        return http.build();
    }
}
