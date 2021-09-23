package com.smartmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cid;
    private String name;
    private String secondName;
    private String work;
    private String email;
    private String phone;
    private String imageUrl;
    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id", nullable = false)
    @JsonIgnore
    private User user;

    public Contact() {
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

//    @Override
//    public String toString() {
//        return "Contact{" +
//                "cid=" + cid +
//                ", name='" + name + '\'' +
//                ", secondName='" + secondName + '\'' +
//                ", work='" + work + '\'' +
//                ", email='" + email + '\'' +
//                ", phone='" + phone + '\'' +
//                ", imageUrl='" + imageUrl + '\'' +
//                ", description='" + description + '\'' +
//                ", user=" + user +
//                '}';
//    }
}
