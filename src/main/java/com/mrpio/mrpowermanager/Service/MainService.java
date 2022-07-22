package com.mrpio.mrpowermanager.Service;

import com.mrpio.mrpowermanager.Model.Status;
import com.mrpio.mrpowermanager.Model.User;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.util.Map;

public class MainService {
    public ResponseEntity<Object> requestStatus(String token) {
        var status = Status.load(token);
        if (status == null)
            return new ResponseEntity<>(new JSONObject(Map.of("error", "unknown user!")), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(status.toJsonObject(), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestSignUp(String token) {
        Serialization serialization = new Serialization(Status.DIR, token + ".dat");
        if (serialization.existFile())
            return new ResponseEntity<>(new JSONObject(Map.of("error", "user already in database!")), HttpStatus.BAD_REQUEST);
        new Status(new User(new Date(System.currentTimeMillis()), token)).save();
        return new ResponseEntity<>(new JSONObject(Map.of("success", "user registered successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestLogin(String token) {
        Serialization serialization = new Serialization(Status.DIR, token + ".dat");
        if (serialization.existFile())
            return new ResponseEntity<>(new JSONObject(Map.of("found", "user present in database!")), HttpStatus.OK);
        return new ResponseEntity<>(new JSONObject(Map.of("notfound", "unknown user!")), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestSleep(String token, Boolean value) {
        var status = Status.load(token);
        if (status == null)
            return new ResponseEntity<>(new JSONObject(Map.of("error", "unknown user!")), HttpStatus.BAD_REQUEST);
        status.setDoSleep(value);
        status.save();
        return new ResponseEntity<>(new JSONObject(Map.of("success", "request received successfully!")), HttpStatus.OK);
    }
}
