package com.mrpio.mrpowermanager.Model;

import com.mrpio.mrpowermanager.Service.Serialization;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class User implements Serializable {
    public final static String DIR = "database/";

    private LocalDateTime signUpDate;
    private String token, email;
    private ArrayList<Pc> pcList;

    public User(LocalDateTime signUp, String token, String email) {
        this.signUpDate = signUp;
        this.token = token;
        this.email = email;
        pcList = new ArrayList<Pc>();
    }

    public LocalDateTime getSignUpDate() {
        return signUpDate;
    }

    public String getToken() {
        return token;
    }

    public void save() {
        Serialization s = new Serialization(DIR, token + ".dat");
        s.saveObject(this);
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Pc> getPcList() {
        return pcList;
    }

    public boolean addPc(String name) {
        for (var pc : pcList)
            if (pc.getName().equals(name))
                return false;
        pcList.add(new Pc(name));
        return true;
    }

    public Pc getPc(String name) {
        for (var pc : pcList)
            if (pc.getName().equals(name))
                return pc;
        return null;
    }

    public static User load(String token) {
        var serialization = new Serialization(DIR, token + ".dat");
        if (serialization.existFile())
            return (User) serialization.loadObject();
        return null;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", this);
        return jsonObject;
    }
}
