package nuts.tech_learning_archive.spring_security_playground.http_security.custom;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

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
