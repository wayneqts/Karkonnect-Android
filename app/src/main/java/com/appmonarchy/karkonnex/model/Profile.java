package com.appmonarchy.karkonnex.model;

import java.io.Serializable;

public class Profile implements Serializable {
    String stt, fName, lName, username, email, phone, phoneStt, address, city, zip, img;

    public Profile(String stt, String fName, String lName, String username, String email, String phone, String phoneStt, String address, String city, String zip, String img) {
        this.stt = stt;
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.phoneStt = phoneStt;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public String getStt() {
        return stt;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoneStt() {
        return phoneStt;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }
}
