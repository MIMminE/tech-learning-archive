# HttpBasic 설정 API [Spring Boot 3.4.5]

클라이언트 - 서버 간 통신에서 HTTP 헤더를 사용하여 사용자 이름, 비밀번호를 전달하는 간단한 인증 방식에 대한 설정이다. 

HTTP 요청 헤더에 Authorization 필드를 추가하여 사용자 이름과 비밀번호를 Base64 데이터 인코딩한 값을 전달한다. 
서버 측에서는 Base64 데이터 디코딩을 사용하여 전달된 인증 정보로 인증을 시도한다. 

주로 폼 로그인을 사용할 수 없는 Rest API 인증에 사용되고 간단하고 빠르다는 장점이 있지만 
보안이 취약해 Https 가 필수적으로 함께 사용되어야 한다.


### HttpSecurity 통한 HttpBasic 설정 
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(basic -> basic
                .realmName("security") // 인증 영역 이름 설정
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\"}"); // JSON 응답 반환
                })
        );
        return http.build(); // SecurityFilterChain 객체 반환
    }
}
```
