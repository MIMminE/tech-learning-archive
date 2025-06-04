# RememberMe 설정 API [Spring Boot 3.4.5]

사용자가 애플리케이션에 로그인한 상태를 유지하도록 돕는 기능이다. 
브라우저를 닫거나 세션이 만료된 후에도 다시 로그인 과정을 거치지 않아도 된다는 편의성을 제공한다.

### RememberMe 쿠키 생성 및 사용 과정
1) 사용자는 로그인과정에서 기억하기 인증 사용 여부를 체크하여 로그인을 시도한다.
2) 로그인에 성공할 경우, 서버는 사용자 이름, 유효 시간과 별도의 비밀 키로 만들어진 서명으로 쿠키를 만든다.
3) 클라이언트는 쿠키로 전달된 값을 다음 요청부터 자동으로 헤더에 담아 보낸다.
4) 서버는 헤더에 remember(기본값) 쿠키를 읽어 서명 검증, 유효 시간 확인 등을 거쳐 해당 쿠키의 사용자 인증 컨텍스트를 복원한다.


### RememberMeAuthenticationFilter 
주로 사용되는 인증 필터와 익명 사용자 인증 필터 사이에 위치하는 편이다. 요청 헤더의 기억하기 쿠키를 확인해 컨텍스트를 복원할 수 있으면 
복원한다. 빌더 클래스에서 rememberMe 메서드가 수행되면 자동으로 필터체인에 등록된다.

### HttpSecurity 통한 기억하기 인증 설정 
```java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.rememberMe(rememberMe -> rememberMe
                .alwaysRemember(true)
                .tokenValiditySeconds(3600)
                .userDetailsService(userDetailService()) // 필수 설정, 반드시 지정해야 함
                .rememberMeParameter("remember") // 폼 로그인의 기억하기 인증 사용 여부 파라미터
                .rememberMeCookieName("remember") // 헤더로 전달되는 기억하기 쿠키 이름
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
```
### UserDetailsService 필수 등록
사용자의 인증 정보를 쿠키를 통해 복원하는 과정에서 어떤 사용자 인증 정보를 사용할지를 명시해주어야 한다.
즉, UserDetailsService 를 명시적으로 등록해주어야만 정상적으로 동작한다. 