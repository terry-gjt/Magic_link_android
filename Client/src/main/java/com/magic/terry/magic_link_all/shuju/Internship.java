package com.magic.terry.magic_link_all.shuju;

import java.io.Serializable;

public class Internship implements Serializable {
    private String name;
    private String money;
    private String address;

    public Internship(String name, String money, String address) {
        this.name = name;
        this.money = money;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}