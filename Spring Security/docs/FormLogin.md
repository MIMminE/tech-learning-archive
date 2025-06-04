# FormLogin 설정 API [Spring Boot 3.4.5]

사용자 이름과 비밀번호를 입력하는 HTML 폼을 통해 인증을 처리하는 방식이다. 웹 애플리케이션에서 가장 일반적으로 사용한다.

기본 로그인 페이지와 로그아웃 페이지를 제공하지만 개발자가 직접 페이지를 작성해서 제공할 수도 있다

### HttpSecurity 통한 FormLogin 설정 
```java

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)// CSRF 보호 비활성화 (개발 중에만, 실제 환경에서는 활성화 필요)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll() // 로그인 페이지 및 정적 리소스 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login") // 커스텀 로그인 페이지 경로
                        .defaultSuccessUrl("/home", true) // 로그인 성공 시 이동할 URL
                        .loginProcessingUrl("/login") // 로그인 프로세스 url
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
```

### 폼 로그인 커스텀 HTML
```html
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <form action="/login" method="post">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username"><br>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password"><br>
        <button type="submit">Login</button>
    </form>
</body>
</html>

```
주의할점은 form 태그의 action url 을 서버 설정(loginProcessingUrl)과 일치해야한다.