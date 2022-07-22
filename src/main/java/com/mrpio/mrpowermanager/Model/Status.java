package com.mrpio.mrpowermanager.Model;

import com.mrpio.mrpowermanager.Service.Serialization;
import org.json.simple.JSONObject;

import java.io.Serializable;

public class Status implements Serializable {
    public final static String DIR = "database/";

    private User user;
    private boolean doSleep;
    private boolean doShutdown;
    private boolean setRed;
    private int redLevel;
    private String doLogin;
    private boolean doSaveBattery;

    public Status(User user) {
        this.user = user;
    }

    public boolean isDoSleep() {
        return doSleep;
    }

    public void setDoSleep(boolean doSleep) {
        this.doSleep = doSleep;
    }

    public boolean isDoShutdown() {
        return doShutdown;
    }

    public void setDoShutdown(boolean doShutdown) {
        this.doShutdown = doShutdown;
    }

    public boolean isSetRed() {
        return setRed;
    }

    public void setSetRed(boolean setRed) {
        this.setRed = setRed;
    }

    public int getRedLevel() {
        return redLevel;
    }

    public void setRedLevel(int redLevel) {
        this.redLevel = redLevel;
    }

    public String getDoLogin() {
        return doLogin;
    }

    public void setDoLogin(String doLogin) {
        this.doLogin = doLogin;
    }

    public boolean isDoSaveBattery() {
        return doSaveBattery;
    }

    public void setDoSaveBattery(boolean doSaveBattery) {
        this.doSaveBattery = doSaveBattery;
    }

    public void save() {
        Serialization s = new Serialization(DIR, user.getToken() + ".dat");
        s.saveObject(this);
    }

    public static Status load(String token) {
        Serialization serialization = new Serialization(Status.DIR, token + ".dat");
        if (serialization.existFile())
            return (Status) serialization.loadObject();
        return null;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", this);
        return jsonObject;
    }
}
