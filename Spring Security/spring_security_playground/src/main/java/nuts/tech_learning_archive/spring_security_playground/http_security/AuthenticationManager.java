package nuts.tech_learning_archive.spring_security_playground.http_security;

import nuts.tech_learning_archive.spring_security_playground.http_security.custom.CustomAuthenticationProvider;
import nuts.tech_learning_archive.spring_security_playground.http_security.custom.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthenticationManager {

    // AuthenticationManagerBuilder 공유 오브젝트를 가져와 필요한 설정 이후 직접 등록하는 방식
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        // 인증 매니저는 인증 프로바이더와 디테일 서비스를 이용하여 인증 로직을 수행한다.
        builder.authenticationProvider(new CustomAuthenticationProvider());
        builder.userDetailsService(new CustomUserDetailsService());

        org.springframework.security.authentication.AuthenticationManager manager = builder.build();

        http.authenticationManager(manager);

        return http.build();
    }


    // 스프링 빈으로 매니저를 등록할 경우 SecurityFilterChain 에 자동으로 등록된다.
    @Bean
    org.springframework.security.authentication.AuthenticationManager customAuthenticationManager(HttpSecurity http) throws Exception {
          AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        // 인증 매니저는 인증 프로바이더와 디테일 서비스를 이용하여 인증 로직을 수행한다.
        builder.authenticationProvider(new CustomAuthenticationProvider());
        builder.userDetailsService(new CustomUserDetailsService());

        return builder.build();
    }
}