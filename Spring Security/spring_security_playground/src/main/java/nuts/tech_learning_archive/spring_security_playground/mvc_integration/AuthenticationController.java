package nuts.tech_learning_archive.spring_security_playground.mvc_integration;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 이 컨트롤러는 스프링 시큐리티에서 제공하는 인증 객체(Authentication, Principal, UserDetails)를
 * 활용하여 현재 인증된 사용자 정보를 조회하는 다양한 방식을 보여줍니다.
 * 실무에서 활용할 떄는 민감 정보가 노출되지 않도록 주의해야 합니다.
 */
@RestController
public class AuthenticationController {

    /**
     * Authentication 객체를 사용하여 현재 인증된 사용자의 정보를 조회하는 예제입니다.
     *
     * @param authentication 스프링 시큐리티에서 제공하는 인증 객체로, 현재 인증된 사용자의 정보를 포함합니다.
     * @return 인증된 사용자의 이름(username)과 권한(authorities)을 문자열로 반환합니다.
     */
    @GetMapping("/auth-info")
    public ResponseEntity<String> getAuthenticationInfo(Authentication authentication) {
        String username = authentication.getName(); // 사용자 이름
        String authorities = authentication.getAuthorities().toString(); // 사용자 권한
        return ResponseEntity.ok("사용자 이름: " + username + ", 권한: " + authorities);
    }

    /**
     * Principal 객체를 사용하여 현재 인증된 사용자의 이름을 조회하는 예제입니다.
     *
     * @param principal 자바의 java.security.Principal 인터페이스로, 인증된 사용자의 이름을 제공합니다.
     * @return 인증된 사용자의 이름(username)을 문자열로 반환합니다.
     */
    @GetMapping("/principal-info")
    public ResponseEntity<String> getPrincipalInfo(Principal principal) {
        return ResponseEntity.ok("현재 사용자: " + principal.getName());
    }

    /**
     * @AuthenticationPrincipal 어노테이션을 사용하여 UserDetails 객체를 직접 주입받는 예제입니다.
     *
     * @param userDetails 스프링 시큐리티에서 제공하는 UserDetails 객체로, 인증된 사용자의 세부 정보를 포함합니다.
     * @return 인증된 사용자의 이름(username)을 문자열로 반환합니다.
     */
    @GetMapping("/user-details")
    public ResponseEntity<String> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok("사용자 이름: " + username);
    }
}
