package com.mrpio.mrpowermanager.Model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private Date signUpDate;
    private String token;

    public User(Date signUp, String token) {
        this.signUpDate = signUp;
        this.token = token;
    }

    public Date getSignUpDate() {
        return signUpDate;
    }

    public String getToken() {
        return token;
    }
}
