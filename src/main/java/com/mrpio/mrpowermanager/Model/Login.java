package com.mrpio.mrpowermanager.Model;

public class Login {
    private String title;
    private String username;
    private String password;
    private String url;
    private String args;

    public Login(String title,String url, String username, String password, String args) {
        this.title = title;
        this.username = username;
        this.password = password;
        this.url = url;
        this.args=args;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArgs() {
        return args;
    }
    public void setArgs(String args) {
        this.args = args;
    }
}
