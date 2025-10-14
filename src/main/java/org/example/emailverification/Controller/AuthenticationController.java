package org.example.emailverification.Controller;

import org.example.emailverification.entity.User;
import org.example.emailverification.service.CustomUserDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class AuthenticationController {

    private final CustomUserDetailService customUserDetailService;

    public AuthenticationController(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @GetMapping
    public String register(Model model){
        User user = new User();
        model.addAttribute("user",user);
        return "register";
    }

    @PostMapping
    public String postUser(@ModelAttribute("user") User user, Model model,
                           RedirectAttributes redirectAttributes){
        // save the user to database
        // send the confirmation email

        customUserDetailService.registerUser(user);
        redirectAttributes.addFlashAttribute(
                "message",
                "Please confirm your email address"
        );
        return "redirect:/register";
    }
}
