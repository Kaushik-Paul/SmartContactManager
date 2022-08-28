package com.smartmanager.controller;

import com.smartmanager.helper.Message;
import com.smartmanager.models.User;
import com.smartmanager.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    //    This is home controller
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    //    This is About controller
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    //    This is signup Controller
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

//    Handler for registering user

    @PostMapping("/do_register")
    public String registerUser(@ModelAttribute("user") User user, @RequestParam("userImage") MultipartFile multipartFile,
                               @RequestParam(value = "agreement", defaultValue = "false")
                                       boolean agreement, Model model, HttpSession session) {
        try {
            if (!agreement) {
                logger.error("You have not agreed the terms and conditions");
                throw new Exception("You have not agreed the terms and conditions");
            }


//            if (result1.hasErrors()) {
//                logger.info("ERROR "+result1.toString());
//                model.addAttribute("user", user);
//                return "signup";
//            }


            User result = homeService.registerUserService(user, multipartFile, session, model);
            logger.trace("Agreement {}", agreement);
            logger.info("USER {}", user.toString());
            model.addAttribute("user", result);
            model.addAttribute("user", new User());
            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong!!!" + e.getMessage(), "alert-danger"));
            return "signup";
        }

    }

//       handler for custom login

    @GetMapping("/signin")
    public String customLogin(Model model) {
        model.addAttribute("title", "Login - Smart Contact Manager");
        return "signin";
    }

    //    Email id form handler
    @GetMapping("/forgotPassword")
    public String openEmailForm(Model model) {

        model.addAttribute("title", "Forgot Password - Smart Contact Manager");
        return "forgot_email_form";
    }

    //    Handler for handling otp
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, Model model) {

        logger.info("EMAIL: {}", email);

//        Generating OTP of 5 digits
        int randomNumber = 10000 + new Random().nextInt(90000);
        logger.info("Random Number : {}", randomNumber);

        model.addAttribute("title", "Verify - OTP");

        return "verify_otp";
    }
}
