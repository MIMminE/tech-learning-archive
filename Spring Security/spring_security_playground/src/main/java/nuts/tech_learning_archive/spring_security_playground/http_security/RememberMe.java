package nuts.tech_learning_archive.spring_security_playground.http_security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Configuration
@EnableWebSecurity
public class RememberMe {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.rememberMe(rememberMe -> rememberMe
                .alwaysRemember(true)
                .tokenValiditySeconds(3600)
                .userDetailsService(userDetailService()) // 필수 설정, 반드시 지정해야 함
                .rememberMeParameter("remember")
                .rememberMeCookieName("remember")
                .key("security") // 실제 운영 환경에서는 보안상 취약할 수 있는 설정이므로 주의해야 한다.
        );
        return http.build();
    }

    @Bean
    UserDetailsService userDetailService() {
        UserDetails user1 = User.builder()
                .username("user1") // 사용자 이름
                .password("{noop}password1") // 비밀번호 (noop은 암호화하지 않음을 의미)
                .roles("USER") // 권한
                .build();

        UserDetails user2 = User.builder()
                .username("admin") // 사용자 이름
                .password("{noop}admin123") // 비밀번호
                .roles("ADMIN") // 권한
                .build();

        return new InMemoryUserDetailsManager(List.of(user1, user2));
    }
}