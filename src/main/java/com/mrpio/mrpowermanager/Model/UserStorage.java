package com.mrpio.mrpowermanager.Model;

import java.util.HashSet;
import java.util.Set;

public class UserStorage {
    private static UserStorage instace;
    private Set<String> users;

    public UserStorage() {
        users=new HashSet<>();
    }

    public static synchronized  UserStorage getInstance(){
        if(instace==null)
            instace=new UserStorage();
        return instace;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(String username) throws Exception {
        if(users.contains(username)){
            throw new Exception("username already in use");
        }
        users.add(username);
    }
}
