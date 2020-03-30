package inquerro.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (!existingAuth.isAuthenticated())
            return "login";
        else
            System.out.println(request.getRequestURI());
        return "login";

    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }
}
