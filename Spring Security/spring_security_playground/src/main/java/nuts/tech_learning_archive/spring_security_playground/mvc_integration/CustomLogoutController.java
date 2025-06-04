package nuts.tech_learning_archive.spring_security_playground.mvc_integration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CustomLogoutController {

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        // 현재 세션 무효화
        request.getSession().invalidate();

        // 시큐리티 컨텍스트 초기화
        SecurityContextHolder.clearContext();

        // 로그아웃 후 로그인 페이지로 리다이렉트
        return "redirect:/login?logout";
    }
}
