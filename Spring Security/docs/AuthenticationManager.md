# AuthenticationManager 설정 API [Spring Boot 3.4.5]

인증을 처리하는 핵심 인터페이스로 내부에 인증 방식에 따라 실제 인증을 처리하는 AuthenticationProvider 들을 가지고 있으며
클라이언트 요청을 받아 처리 가능한 Provider 에게 위임하는 역할을 수행한다.

보안 필터 체인의 인증 관련 필터들은 내부 필드에 인증 매니저를 지니게 된다. 필터들은 자신에게 온 요청을 매니저에게 전달함으로써 현재 요청이 인증받은 요청인지를 확인하게 된다.

### AuthenticationManager 인스턴스 생성  
```java
public interface AuthenticationManager {
    Authentication authenticate(Authentication authentication) throws AuthenticationException;
}

```
인증 매니저는 별도의 인터페이스를 제공하고 있기는 하지만 해당 인터페이스를 사용하는 방식보다 인증 매니저에 주입할 요소들을 커스텀하는 방식으로 생성하는 편이다.
이는 실제 매니저의 역할이 단순히 AuthenticationProvider 에 인증을 위임하는 역할만 하기 때문이다. 

### HttpSecurity 통한 인증 매니저 설정 
```java
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

        AuthenticationManager manager = builder.build();

        http.authenticationManager(manager);

        return http.build();
    }
}

```

### AuthenticationManager 빈을 직접 등록하는 방법
```java
@Configuration
@EnableWebSecurity
public class AuthenticationManager {
    
    // 스프링 빈으로 매니저를 등록할 경우 SecurityFilterChain 에 자동으로 등록된다.
    @Beanm
    AuthenticationManager customAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        // 인증 매니저는 인증 프로바이더와 디테일 서비스를 이용하여 인증 로직을 수행한다.
        builder.authenticationProvider(new CustomAuthenticationProvider());
        builder.userDetailsService(new CustoUserDetailsService());

        return builder.build();
    }
}
```
인증 매니저 빈을 직접 등록하는 경우, 스프링 시큐리티 자동 설정에 의해 등록되는 기본 인증 필터들은 해당 빈을 스프링 컨테이너를 통해 자동으로 참조할 수 있다.
하지만 커스텀으로 필터를 추가하는 경우에는 자동 참조가 불가능하며 수동으로 주입시켜주어야 한다는 것을 주의해야 한다.

