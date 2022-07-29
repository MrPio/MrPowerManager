package com.mrpio.mrpowermanager.Service;

import com.mrpio.mrpowermanager.Controller.Controller;
import com.mrpio.mrpowermanager.Model.*;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class MainService {

    public ResponseEntity<Object> requestSignUp(String token, String email) {
        Serialization serialization = new Serialization(User.DIR, token + ".dat");
        if (serialization.existFile())
            return new ResponseEntity<>(new JSONObject(Map.of("result", "user already in database!")), HttpStatus.OK);
        (new User(LocalDateTime.now(), token, email)).save();
        return new ResponseEntity<>(new JSONObject(Map.of("result", "user registered successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestLogin(String token) {
        var user = User.load(token);
        if (user != null) {
            var response = new JSONObject(Map.of("result", "user present in database!", "user", user.toJsonObject()));
            response.put("user", user.toJsonObject().values().toArray()[0]);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestAddPc(String token, String name) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);
        else {
            if (!user.addPc(name))
                return new ResponseEntity<>(new JSONObject(Map.of("result", "there is already a pc with that name!")), HttpStatus.OK);
            user.save();
            return new ResponseEntity<>(new JSONObject(Map.of("result", "pc added successfully!")), HttpStatus.OK);
        }
    }


    public ResponseEntity<Object> requestGetPcStatus(String token, String pcName) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);
        else {
            var pc = user.getPc(pcName);
            if (pc == null)
                return new ResponseEntity<>(new JSONObject(Map.of("result", "pc not found!")), HttpStatus.OK);
            else
                return new ResponseEntity<>(new JSONObject(Map.of("result", "pc state gotten successfully!", "value", pc.getState())), HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> requestScheduleCommand(String token, String pcName, Command.Commands command, String scheduleDate) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);
        else {
            var pc = user.getPc(pcName);
            if (pc == null)
                return new ResponseEntity<>(new JSONObject(Map.of("result", "pc not found!")), HttpStatus.OK);
            else {
                var now = LocalDateTime.now();
                var scheduleDateNew = scheduleDate == null ? now : Controller.stringToLocalDate(scheduleDate);
                var result = pc.addCommand(new Command(command, now, scheduleDateNew));
                user.save();
                return new ResponseEntity<>(new JSONObject(Map.of("result", result)), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity<Object> requestAvailableCommands(String token, String pcName) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);
        else {
            var pc = user.getPc(pcName);
            if (pc == null)
                return new ResponseEntity<>(new JSONObject(Map.of("result", "pc not found!")), HttpStatus.OK);
            else {
                var result = pc.listAvailableCommands();
                user.save();
                var httpStatus = HttpStatus.OK;
                if (result.size() == 0)
                    httpStatus = HttpStatus.NO_CONTENT;
                return new ResponseEntity<>(new JSONObject(Map.of("result", "list received successfully!",
                        "commands", result)), httpStatus);
            }
        }
    }

    public ResponseEntity<Object> requestEndCommand(String token, String pcName, Integer id) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);
        else {
            var pc = user.getPc(pcName);
            if (pc == null)
                return new ResponseEntity<>(new JSONObject(Map.of("result", "pc not found!")), HttpStatus.OK);
            else {
                var result = pc.endCommand(id);
                user.save();
                return new ResponseEntity<>(new JSONObject(Map.of("result", result)), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity<Object> requestCode(String token, String pcName) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);
        else {
            var code = Code.generateCode(token, pcName);
            return new ResponseEntity<>(new JSONObject(Map.of("result", "code generated successfully!", "code", code)), HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> requestValidateCode(String code) {
        return Code.validateCode(code);

    }

    public ResponseEntity<Object> requestUpdatePcStatus(String token, String pcName, PcStatus pcStatus) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);
        else {
            var pc = user.getPc(pcName);
            if (pc == null)
                return new ResponseEntity<>(new JSONObject(Map.of("result", "pc not found!")), HttpStatus.OK);
            else {
                pc.updatePcStatus(pcStatus);
                user.save();
                return new ResponseEntity<>(new JSONObject(Map.of("result", "pc status updated successfully!")), HttpStatus.OK);
            }
        }
    }

}

