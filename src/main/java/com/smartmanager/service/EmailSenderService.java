package com.smartmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sendEmailFrom;

    @Value("${external-data.welcome-email-gif-path}")
    private String welcomeEmailGif;

//    Logger myLogger = LoggerFactory.getLogger(EmailSenderService.class);

    public void sendSimpleEmail(String toEmail, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(sendEmailFrom);

        mailSender.send(message);

        System.out.println("Email Send !!! " + message);
    }


    public void sendEmailWithAttachment(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(sendEmailFrom);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body);

        FileSystemResource fileSystem = new FileSystemResource(new File(welcomeEmailGif));

        mimeMessageHelper.addAttachment(fileSystem.getFilename(), fileSystem);

        mailSender.send(mimeMessage);

        System.out.println("Email Send !!! " + mimeMessage);
    }
}
