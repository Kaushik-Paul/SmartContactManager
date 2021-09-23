package com.smartmanager.service;

import com.smartmanager.controller.UserController;
import com.smartmanager.helper.Message;
import com.smartmanager.models.Contact;
import com.smartmanager.models.User;
import com.smartmanager.repository.ContactRepository;
import com.smartmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContactRepository contactRepository;

    @Value("${external-data.contact-file-path}")
    private String contactProfilePicPath;


    //    Service to send the common user data
    public User addCommonDataService(Principal principal) {
        String userName = principal.getName();
//        System.out.println("USERNAME : "+userName);

        //   getting the user using email
        User user = userRepository.getUserByUserName(userName);
        return user;
    }


//    Service for adding contact

    public void processContactService(Contact contact, MultipartFile file, Principal principal, HttpSession session) throws IOException {
        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);

//            processing and uploading file

        if (file.isEmpty()) {
            System.out.println("File is empty");
            contact.setImageUrl("contact.png");
        } else {
//                uploading the file
//                contact.setImageUrl(file.getOriginalFilename());
//                File saveFile = new ClassPathResource("static/profilephotos").getFile();
//                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
//                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("Image is uploading");


//                File convertFile = new File("E:\\Codes\\Spring Boot Projects\\Smart Contact Manager\\src\\main\\resources\\static\\profilephotos\\" + file.getOriginalFilename());
            String fileName = Instant.now().getEpochSecond() + file.getOriginalFilename();
            File convertFile = new File(contactProfilePicPath + fileName);
            convertFile.createNewFile();
            OutputStream os = new FileOutputStream(convertFile);
            os.write(file.getBytes());
            contact.setImageUrl(fileName);
        }

        contact.setUser(user);

        user.getContacts().add(contact);

        this.userRepository.saveAndFlush(user);

        System.out.println("Added to data base");
        System.out.println("CONTACT DATA " + contact);
        LOGGER.info("Contact Data {}", contact);
        session.setAttribute("message", new Message("Your Contact has been Added!!!!", "alert-success"));
    }


//    Service for pagination

    public Page<Contact> showContactsService(int page, Principal principal) {
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page, 5);

        Page<Contact> contacts = contactRepository.findContactByUser(user.getUid(), pageable);
        return contacts;
    }


//    Service for showing particular Contact

    public void showContactDetailService(int cId, Principal principal, Model model) {
        //        System.out.println("CID " + cId);

        System.out.println(bCryptPasswordEncoder.encode("root"));
        Optional<Contact> contactOptional = contactRepository.findById(cId);
        Contact contact = contactOptional.get();

//        To get which user is logged in
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);
        model.addAttribute("title", contact.getName());

        if (user.getUid() == contact.getUser().getUid()) {
            model.addAttribute("contact", contact);
        }
    }

//    Service for deleting Contact

    public void deleteContactService(int cId, Principal principal, HttpSession session) {
        Optional<Contact> contactOptional = contactRepository.findById(cId);
        Contact contact = contactOptional.get();
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

//        checking for unauthorised deletion
        if (user.getUid() == contact.getUser().getUid()) {

//            deleting the photo

            File deleteFile = new File(contactProfilePicPath + contact.getImageUrl());
            if (!contact.getImageUrl().equals("contact.png")) {
                deleteFile.delete();
            }

//            deleting from database
            contact.setUser(null);
            contactRepository.delete(contact);
            session.setAttribute("message", new Message("Contact deleted successfully", "alert-success"));
        } else {
            session.setAttribute("message", new Message("Something went wrong!!! ", "alert-danger"));
        }
    }

//    Service for updateForm Opening

    public void updateFormService(int cid, Model model, Principal principal) {
        Contact contact = contactRepository.findById(cid).get();
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        //Checking for unauthorized contact updation
        if (user.getUid() == contact.getUser().getUid()) {
            model.addAttribute("Title", contact.getName());
            model.addAttribute("contact", contact);
        }
    }


    //    Service for updating contact
    public void updateContactHandlerService(Contact contact, MultipartFile multipartFile,
                                            HttpSession session, Principal principal) throws IOException {

        Contact oldContactDetail = contactRepository.findById(contact.getCid()).get();
        //Image updation
        if (!multipartFile.isEmpty()) {

//                updating to new photo

            String fileName = Instant.now().getEpochSecond() + multipartFile.getOriginalFilename();
            File convertFile = new File(contactProfilePicPath + fileName);
            convertFile.createNewFile();
            OutputStream os = new FileOutputStream(convertFile);
            os.write(multipartFile.getBytes());
            contact.setImageUrl(fileName);
//                deleting old photo
            File deleteFile = new File(contactProfilePicPath + oldContactDetail.getImageUrl());
            if (!oldContactDetail.getImageUrl().equals("contact.png")) {
                deleteFile.delete();
            }


        } else {
            contact.setImageUrl(oldContactDetail.getImageUrl());
        }
        User user = userRepository.getUserByUserName(principal.getName());
        contact.setUser(user);
        contactRepository.save(contact);
        session.setAttribute("message", new Message("Your Contact is Updated!!!!", "alert-success"));

    }


    public boolean changePassword(String newUserName, String oldPassword, String newPassword, String retypeNewPassword,
                                  Principal principal, HttpSession session) {

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        //change the UserName
        if (!newUserName.isEmpty()) {
            user.setEmail(newUserName);
        }

        if (oldPassword.isEmpty() && newPassword.isEmpty() && retypeNewPassword.isEmpty() && !newUserName.isEmpty()) {
            userRepository.save(user);
            session.setAttribute("message", new Message("Your UserName is successfully changed!!!", "alert-success"));
            return true;
        }

        if (this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword()) && newPassword.equals(retypeNewPassword)) {

//            Change the password
            user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(user);
            if (newUserName.isEmpty()) {
                session.setAttribute("message", new Message("Your Password is successfully changed!!!", "alert-success"));
                return true;
            } else {
                session.setAttribute("message",
                        new Message("Your UserName and Password is successfully changed!!!", "alert-success"));
                return true;
            }
        } else {
            session.setAttribute("message", new Message("Password not matching!!!", "alert-danger"));
            return false;
        }


    }
}
