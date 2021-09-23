package com.smartmanager.controller;

import com.smartmanager.helper.Message;
import com.smartmanager.models.Contact;
import com.smartmanager.models.User;
import com.smartmanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Value("${external-data.contact-file-path}")
    private String contactProfilePicPath;

    //    method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {

        User user = userService.addCommonDataService(principal);
//        System.out.println("USER : " + user);
        model.addAttribute("user", user);
    }

    //    Dashboard Home
    @GetMapping("/index")
    public String dashboard(Model model) {

        model.addAttribute("title", "User Dashboard");
        return "user/user_dashboard";
    }

//    open add form handler

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact - Smart Contact Manager");
        model.addAttribute("contact", new Contact());

        return "user/add_contact_form";
    }

//    Processing add contact form

    @PostMapping("/process_contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("image") MultipartFile file,
                                 Principal principal, HttpSession session) {

        try {

            userService.processContactService(contact, file, principal, session);
            return "user/add_contact_form";

        } catch (Exception e) {
            System.out.println("ERROR" + e.getMessage());
            LOGGER.error(e.getMessage(), e);
            session.setAttribute("message", new Message("Something went wrong!!!" + e.getMessage(), "alert-danger"));
            return "user/add_contact_form";
        }
    }

//    show contacts handler
//    per page =5[n]
//        current page 0

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable int page, Model model, Principal principal) {
        model.addAttribute("title", "View Contacts");

//        Have to send contact list

        Page<Contact> contacts = userService.showContactsService(page, principal);

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());

        return "user/show_contacts";
    }

//    showing particular contact detail

    @GetMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable int cId, Model model, Principal principal) {

        userService.showContactDetailService(cId, principal, model);

        return "user/contact_detail";
    }

//    delete contact handler

    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable int cId, Principal principal, HttpSession session) {
        try {
            userService.deleteContactService(cId, principal, session);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/user/show-contacts/0";
    }

//    Open Update form handle

    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable int cid, Model model, Principal principal) {
        userService.updateFormService(cid, model, principal);
        return "user/update_form";
    }

    //    update contact handler
    @PostMapping("/process-update")
    public String updateContactHandler(@ModelAttribute Contact contact, @RequestParam("image") MultipartFile multipartFile,
                                       HttpSession session, Principal principal) {
        try {
            userService.updateContactHandlerService(contact, multipartFile, session, principal);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong!!! " + e.getMessage(), "alert-danger"));

        }
        return "redirect:/user/contact/" + contact.getCid();
    }

//    your profile handler

    @GetMapping("/profile")
    public String yourProfile(Model model, Principal principal) {
        model.addAttribute("title", "Profile Page");
        User user = userService.addCommonDataService(principal);
        model.addAttribute("user", user);
        return "user/profile";
    }

    //Open Settings Handler

    @GetMapping("/settings")
    public String openSettings(Model model, Principal principal) {
        model.addAttribute("title", "Settings");
        model.addAttribute("userName", principal.getName());
        return "user/settings";
    }

//    Change Password Handler

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newUserName, @RequestParam String oldPassword, @RequestParam String newPassword,
                                 @RequestParam String retypeNewPassword,
                                 Principal principal, HttpSession session) {

        System.out.println("Old Password " + oldPassword);
        System.out.println("New Password " + newPassword);

        boolean isSuccessfull = userService.changePassword(newUserName, oldPassword, newPassword, retypeNewPassword,
                principal, session);
//        model.addAttribute("userName", principal.getName());
        if (newUserName.isEmpty()) {
            if (isSuccessfull) {
                return "redirect:/user/profile";
            } else {
                return "redirect:/user/settings";
            }
        } else {
            return "redirect:/logout";
        }
    }

}


