package com.mrpio.mrpowermanager.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mrpio.mrpowermanager.Controller.Controller;
import com.mrpio.mrpowermanager.Service.DropboxApi;
import com.mrpio.mrpowermanager.Service.Serialization;
import org.json.simple.JSONObject;
import org.springframework.scheduling.annotation.Async;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class User implements Serializable {
    public final static String DIR = "database/";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime signUpDate;
    private final String token;
    private final String email;
    private final ArrayList<Pc> pcList;
    private transient boolean scheduled;
    private transient boolean isClientOnline;
    private transient LocalDateTime lastClientOnline;


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

    @Async
    public void scheduleSave(boolean... force) {
        var start = System.nanoTime();

        if (force.length > 0 && force[0]) {
            new Thread(this::save).start();
            return;
        }

        if (!scheduled) {
            System.out.println("Saving...");
            Executors.newScheduledThreadPool(1).schedule(this::save, 300, TimeUnit.SECONDS);
            scheduled = true;
        }
        System.out.println("took ---> " + (System.nanoTime() - start) / 1000000d);
    }

    private void save() {

        Serialization s = new Serialization(DIR, token + ".dat");
        s.saveObject(this);
        DropboxApi.uploadFile(
                s.getFullPath(),
                "/database/" + s.getFileName());
        System.out.println("Saved");
        scheduled = false;
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
        //300~350 times faster with this
        for (var user : Controller.usersCache)
            if (user.getToken().equals(token))
                return user;

        if (Controller.usersCache.size() > 10000)
            Controller.usersCache = Controller.usersCache.subList(2000, Controller.usersCache.size());

        var serialization = new Serialization(DIR, token + ".dat");
        if (serialization.existFile()) {
            var user = (User) serialization.loadObject();
            Controller.usersCache.add(user);
            return user;
        }


        String path = "/database";
        if (DropboxApi.getFilesInFolder(path).contains(token + ".dat")) {
            DropboxApi.downloadFile(
                    path + "/" + token + ".dat",
                    DIR + token + ".dat");
            var user = (User) serialization.loadObject();
            Controller.usersCache.add(user);
            return user;
        }

        return null;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", this);
        return jsonObject;
    }

    public boolean removePc(String pcName) {
        return pcList.removeIf(pc -> pc.getName().equals(pcName));
    }

    public void clientGoOnline() {
        lastClientOnline=LocalDateTime.now(ZoneOffset.UTC);
        if (!isClientOnline) {
            var s = new Serialization(DIR + "clients", token + ".user");
            s.saveObject("online");
            new Thread(() -> DropboxApi.uploadFile(s.getFullPath(),
                    "/database/clients/" + s.getFileName())).start();
            isClientOnline=true;
            scheduleGoOffline();
        }
    }

    @Async
    void scheduleGoOffline(){
        Executors.newScheduledThreadPool(1).schedule(
                ()->{
                    if(SECONDS.between(lastClientOnline,LocalDateTime.now(ZoneOffset.UTC))<35){
                        System.out.println("rimando...");
                        scheduleGoOffline();
                        return;
                    }
                    System.out.println("elimino...");
                    DropboxApi.deleteFile("/database/clients/" + token + ".user");
                    isClientOnline=false;
                },
                30, TimeUnit.SECONDS);

    }
}
