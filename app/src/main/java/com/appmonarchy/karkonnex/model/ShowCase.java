package com.appmonarchy.karkonnex.model;

public class ShowCase {
    String name, address, price;
    int img;

    public ShowCase(String name, String address, String price, int img) {
        this.name = name;
        this.address = address;
        this.price = price;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }

    public int getImg() {
        return img;
    }

}
