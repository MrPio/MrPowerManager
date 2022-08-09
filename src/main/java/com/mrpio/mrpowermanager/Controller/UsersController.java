package com.mrpio.mrpowermanager.Controller;

import com.mrpio.mrpowermanager.Model.UserStorage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class UsersController {
    @GetMapping("/registration/{userName}")
    public ResponseEntity<Void> register(@PathVariable String userName){
        System.out.println("registering");
        try {
            UserStorage.getInstance().setUsers(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok().build();
    }

    @GetMapping("/fetchAllUsers")
    public Set<String> fetchAll(){
        return UserStorage.getInstance().getUsers();
    }
}
