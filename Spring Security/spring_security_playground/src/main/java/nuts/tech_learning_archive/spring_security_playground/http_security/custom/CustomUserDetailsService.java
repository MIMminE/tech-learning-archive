package nuts.tech_learning_archive.spring_security_playground.http_security.custom;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

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
