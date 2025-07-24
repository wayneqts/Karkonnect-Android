package com.appmonarchy.karkonnex.model;

import java.io.Serializable;

public class CarInfo implements Serializable {
    String id, pId, type, uId, phone, email, address, des, make, modal, year, priceDay, priceWeek, priceMonth, img1, img2, img3, img4, city, zip, state, country, username, created;

    public CarInfo(String id, String pId, String type, String uId, String phone, String email, String address, String des, String make, String modal, String year, String priceDay, String priceWeek, String priceMonth, String img1, String img2, String img3, String img4, String city, String zip, String state, String country, String created) {
        this.id = id;
        this.pId = pId;
        this.type = type;
        this.uId = uId;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.des = des;
        this.make = make;
        this.modal = modal;
        this.year = year;
        this.priceDay = priceDay;
        this.priceWeek = priceWeek;
        this.priceMonth = priceMonth;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.city = city;
        this.zip = zip;
        this.state = state;
        this.country = country;
        this.created = created;
    }

    public String getCreated() {
        return created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getId() {
        return id;
    }

    public String getpId() {
        return pId;
    }

    public String getType() {
        return type;
    }

    public String getuId() {
        return uId;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getDes() {
        return des;
    }

    public String getMake() {
        return make;
    }

    public String getModal() {
        return modal;
    }

    public String getYear() {
        return year;
    }

    public String getPriceDay() {
        return priceDay;
    }

    public String getPriceWeek() {
        return priceWeek;
    }

    public String getPriceMonth() {
        return priceMonth;
    }

    public String getImg1() {
        return img1;
    }

    public String getImg2() {
        return img2;
    }

    public String getImg3() {
        return img3;
    }

    public String getImg4() {
        return img4;
    }
}
