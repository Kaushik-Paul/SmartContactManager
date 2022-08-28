package com.smartmanager.service;

import com.smartmanager.helper.Message;
import com.smartmanager.helper.StaticEmailInfo;
import com.smartmanager.models.User;
import com.smartmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

@Service
public class HomeService {

    @Value("${external-data.user-file-path}")
    private String userProfilePicPath;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailSenderService emailSenderService;


//    Service to add new User

    public User registerUserService(User user, MultipartFile multipartFile, HttpSession session, Model model) throws IOException, MessagingException {
        user.setRole("ROLE_USER");
        user.setEnabled(true);

//            Setting up of user image
        if (multipartFile.isEmpty()) {
            user.setImageUrl("user.png");
        } else {
            String fileName = Instant.now().getEpochSecond() + multipartFile.getOriginalFilename();
            File convertFile = new File(userProfilePicPath + fileName);
            convertFile.createNewFile();
            OutputStream os = new FileOutputStream(convertFile);
            os.write(multipartFile.getBytes());
            user.setImageUrl(fileName);
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        session.setAttribute("message", new Message("Successfully Registered!!!!", "alert-success"));

        // Send Registration Successful mail to sender
        String subject = StaticEmailInfo.welcomeEmailSubject;
        String body = StaticEmailInfo.welcomeEmailBody;

        emailSenderService.sendEmailWithAttachment(user.getEmail(), subject, body);
        return result;
    }
}
