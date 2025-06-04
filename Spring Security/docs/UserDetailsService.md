# UserDetailsService 설정 API [Spring Boot 3.4.5]

사용자 인증을 처리하기 위해 제공되는 핵심 인터페이스이다. 사용자 정보를 가져오는 역할을 하며 인증 과정에서 
사용자 이름(username)을 기반으로 사용자 정보를 조회하는 데 사용된다. 

기본으로 제공해주는 구현체 또는 커스텀 구현체로 데이터베이스 또는 다른 저장소에서 사용자 정보를 가져올 수 있다.
조회된 사용자는 스프링 시큐리티에 표준화되어 있는 UserDetails 객체로 변환되어 제공된다.

### UserDetailsService 인터페이스 구현
```java
public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users = new HashMap<>();

    public CustomUserDetailsService() {
        users.put("admin", User.builder()
                .username("admin")
                .password("{noop}password") // 비밀번호는 "{noop}"으로 인코딩 처리 (평문)
                .roles("ADMIN") // ROLE_ADMIN 권한 부여
                .build());

        users.put("user", User.builder()
                .username("user")
                .password("{noop}password") // 비밀번호는 "{noop}"으로 인코딩 처리 (평문)
                .roles("USER") // ROLE_USER 권한 부여
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetails user = users.get(username);

        if (user == null) {
            // 사용자를 찾을 수 없으면 예외 발생
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return user;
    }
}
```


### HttpSecurity 통한 UserDetailsService 설정 
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // HttpSecurity 빌더 클래스를 통한 디테일 서비스 직접 등록하여 필터 체인 등록하는 방법
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.userDetailsService(new CustomUserDetailsService());

        return http.build();
    }
}

```

### UserDetailsService 빈으로 직접 등록
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // 디테일 서비스 구현체를 스프링 빈으로 등록함으로써 자동으로 필터 체인에 등록되게 하는 방법
    @Bean
    UserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }
}
```

### AuthenticationManager 와 UserDetailsService 관계
빌더를 통하든 빈으로 등록하여 자동 구성을 통하든 UserDetailsService 는 필터에 직접 등록되는 것이 아니다.
정확하게 따지면 AuthenticationManager 가 AuthenticationProvider 를 호출하고, AuthenticationProvider 가 UserDetailsService 를
사용하여 사용자 인증 정보를 조회하는 것이다. 

이 과정을 추상화하여 제공해주는 것이다.

