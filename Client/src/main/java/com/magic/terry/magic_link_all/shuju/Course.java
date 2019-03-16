package com.magic.terry.magic_link_all.shuju;

import java.io.Serializable;

public class Course implements Serializable {
    private String name;
    private String money;

    public Course(String name, String money) {
        this.name = name;
        this.money = money;
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
}