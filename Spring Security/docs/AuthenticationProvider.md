# AuthenticationProvider 설정 API [Spring Boot 3.4.5]

사용자의 인증 요청을 처리하는 역할을 수행하는 스프링 시큐리티 클래스,
여러 AuthenticationProvider 구햔체를 등록하여 각각의 인증 방식을 처리할 수 있게 한다.

대표적인 구현체로 DaoAuthenticationProvider 가 있다.

### 요청 이후 AuthenticationProvider 까지 전달되기까지의 과정
1) 클라이언트 요청
2) SecurityFilterChain 에 등록되어 있는 인증처리를 위한 필터에 요청 위임 (예, UsernameAuthenticationFilter)
3) 요청에서 인증 시도에 필요한 정보를 필터 내의 AuthenticationManager 에 전달
4) AuthenticationManager 내부에서 적절한 AuthenticationProvider 를 찾아 인증 처리 위임


### AuthenticationProvider 인터페이스 구현체 생성
```java
import org.springframework.security.authentication.AuthenticationProvider;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 간단한 인증 로직 (예: 사용자 이름과 비밀번호 확인)
        if ("admin".equals(username) && "password".equals(password)) {
            return new UsernamePasswordAuthenticationToken(username, password, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }

        throw new BadCredentialsException("Invalid username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 인증 객체의 클래스 메타 정보를 가지고 실제 어떤 구현체의 토큰인지를 확인하는 방식으로
        // 주로 사용되는 패턴이다.
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

### SecurityFilterChain 빌더 과정에서의 설정
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(new CustomAuthenticationProvider());

        return http.build();
    }
}
```
build 메서드가 호출되어 SecurityFilterChain 인스턴스가 생성되는 과정에서 AuthenticationManager 에 등록된다.


### AuthenticationProvider 빈을 직접 등록하는 방법
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // 스프링 빈으로 등록하게 되면 자동으로 필터 체인에 등록된다.
    @Bean
    AuthenticationProvider customAuthenticationProvider() {
        return new AuthenticationProvider();
    }
}
```
프로바이더를 빈으로 등록하는 방식은 별도의 AuthenticationManager 를 직접 등록하는 부분이 없을경우에만 정상적으로 동작한다. 
그 이유는 빈으로 등록되어 있는 프로바이더는 찾아 하나씩 등록해주는 로직 자체가 매니저 자동 구성 부분에 들어가 있는데,
개발자가 직접 매니저를 생성하여 빈으로 등록하는 경우 커스텀 프로바이더는 직접 등록해주어야 한다.
    
