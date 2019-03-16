package com.magic.terry.magic_link_all.shuju;

import java.io.Serializable;

public class Friends implements Serializable {
    private String username;


    private int id;
    private String classify;

    public Friends(int id, String username, String classify) {
        this.id = id;
        this.username = username;
        this.classify = classify;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }
}
