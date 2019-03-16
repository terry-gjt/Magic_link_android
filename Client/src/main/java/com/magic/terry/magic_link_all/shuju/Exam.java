package com.magic.terry.magic_link_all.shuju;

import java.io.Serializable;

public class Exam implements Serializable {
    private String name;
    private String time;
    private String asOfTheDate;

    public Exam(String name, String time, String asOfTheDate) {
        this.name = name;
        this.time = time;
        this.asOfTheDate = asOfTheDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAsOfTheDate(String asOfTheDate) {
        this.asOfTheDate = asOfTheDate;
    }

    public String getAsOfTheDate() {
        return asOfTheDate;
    }
}