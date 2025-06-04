# Anonymous 설정 API [Spring Boot 3.4.5]

스프링 시큐리티는 요청에 대한 인증 객체를 SecurityContextHolder 에 저장하고 관리하는데,
인증을 받지 못한 최초의 사용자들에 대해서도 Anonymous 라고 하는 익명 사용자 인증 객체를 제공한다. 
실제 인증 토큰 클래스는 AnonymousAuthenticationToken 이다.

### AnonymousAuthenticationFilter
필처 체인 내의 위치는 인증 필터들 다음에 위치한다. 이는 인증 필터들을 모두 거쳤음에도 보안 컨텍스트에
인증 객체가 없을 경우 익명 인증 객체를 제공하기 위함이다.

### 익명 사용자 인증 객체 설정
```java

@Configuration
@EnableWebSecurity
public class AnonymousConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.anonymous(anonymous -> anonymous
                .principal("guest") // 익명 사용자의 주체의 값을 guest로 설정한다. 기본값은 anonymousUser 이다.
                .authorities("ROLE_GUEST") // 익명 사용자에게 "ROLE_GUEST" 권한을 부여한다.
        );
        return http.build();
    }
}
```