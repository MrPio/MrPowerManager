package com.mrpio.mrpowermanager.Service;

import com.mrpio.mrpowermanager.Controller.Controller;
import com.mrpio.mrpowermanager.Model.*;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.mrpio.mrpowermanager.Model.User.DIR;
import static java.time.temporal.ChronoUnit.MINUTES;

public class MainService {

    private Object validateUserAndPc(String token, String pcName) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);
        else {
            var pc = user.getPc(pcName);
            if (pc == null)
                return new ResponseEntity<>(new JSONObject(Map.of("result", "pc not found!")), HttpStatus.OK);
            return new Object[]{user, pc};
        }
    }

    public ResponseEntity<Object> requestSignUp(String token, String email) {
        var user = User.load(token);
        if (user != null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "user already in database!")), HttpStatus.OK);
        (new User(LocalDateTime.now(), token, email)).scheduleSave(true);
        return new ResponseEntity<>(new JSONObject(Map.of("result", "user registered successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestLogin(String token, boolean imTheClient) {
        var user = User.load(token);
        if (user != null) {
            var response = new JSONObject(Map.of("result", "user present in database!", "user", user.toJsonObject()));
            response.put("user", user.toJsonObject().values().toArray()[0]);
            if (imTheClient)
                user.clientGoOnline();
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
            user.scheduleSave();
            return new ResponseEntity<>(new JSONObject(Map.of("result", "pc added successfully!")), HttpStatus.OK);
        }
    }//DEPRECATED

    public ResponseEntity<Object> requestGetPcStatus(String token, String pcName) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var pc = (Pc) ((Object[]) result)[1];

        return new ResponseEntity<>(new JSONObject(Map.of("result", "pc state gotten successfully!", "value", pc.getState())), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestScheduleCommand(String token, String pcName, Command.Commands command, String scheduleDate) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        var now = LocalDateTime.now();
        var scheduleDateNew = scheduleDate == null ? now : Controller.stringToLocalDate(scheduleDate);
        var request = pc.addCommand(new Command(command, now, scheduleDateNew));
        user.scheduleSave();
        ;
        return new ResponseEntity<>(new JSONObject(Map.of("result", request)), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestAvailableCommands(String token, String pcName) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        var commands = pc.listAvailableCommands();
        user.scheduleSave();
        ;
        var httpStatus = HttpStatus.OK;
        if (commands.size() == 0)
            httpStatus = HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(new JSONObject(Map.of("result", "list received successfully!",
                "commands", commands)), httpStatus);


    }

    public ResponseEntity<Object> requestEndCommand(String token, String pcName, Integer id) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        var command = pc.endCommand(id);
        user.scheduleSave();
        ;
        return new ResponseEntity<>(new JSONObject(Map.of("result", command)), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestCode(String token, String pcName) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);

        if (user.getPc(pcName) != null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "this pc name is already in use!")), HttpStatus.OK);

        var code = Code.generateCode(token, pcName);
        return new ResponseEntity<>(new JSONObject(Map.of("result", "code generated successfully!", "code", code)), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestValidateCode(String code) {
        return Code.validateCode(code);

    }

    public ResponseEntity<Object> requestUpdatePcStatus(String token, String pcName, PcStatus pcStatus) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        pc.updatePcStatus(pcStatus);
        user.scheduleSave();
        return new ResponseEntity<>(new JSONObject(Map.of("result", "pc status updated successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestUploadWattageEntries(String token, String pcName, WattageEntry[] wattageEntries) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        pc.uploadWattageEntries(wattageEntries);
        user.scheduleSave();
        return new ResponseEntity<>(new JSONObject(Map.of("result", "wattage entries uploaded successfully!")), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestStorePassword(String token, String pcName, String title, String password) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        var updated = pc.storePassword(title, password);
        user.scheduleSave();

        return new ResponseEntity<>(new JSONObject(Map.of("result",
                updated ? "password updated successfully!" : "password stored successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestDeletePassword(String token, String pcName, String title) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        var password = pc.deletePassword(title);
        user.scheduleSave();

        return new ResponseEntity<>(new JSONObject(Map.of("result",
                password == null ? "password not found!" : "password deleted successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestSendKey(String token, String pcName, String title, String key) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        var updated = pc.storeKey(title, key);
        user.scheduleSave();
        return new ResponseEntity<>(new JSONObject(Map.of("result",
                updated ? "key updated successfully!" : "key stored successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestRequestKey(String token, String pcName, String title) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        var key = pc.requestKey(title);
        user.scheduleSave();
        var map = key == null ? Map.of("result", "key not found!") :
                Map.of("result", "key requested successfully!", "key", key);

        return new ResponseEntity<>(new JSONObject(map), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestDeleteAccount(String token, String email) {
        var user = User.load(token);
        if (user == null)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "unknown user!")), HttpStatus.OK);

        if (user.getEmail().trim().equals(email.trim())) {
            var file = new File(DIR, token + ".dat");
            file.delete();
            new Thread(() -> DropboxApi.deleteFile("/database/" + token + ".dat")).start();
            return new ResponseEntity<>(new JSONObject(Map.of("result", "account deleted successfully!")), HttpStatus.OK);
        }
        return new ResponseEntity<>(new JSONObject(Map.of("result", "the email wasn't correct!")), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestDeletePc(String token, String pcName) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        user.removePc(pcName);
        user.scheduleSave();
        ;
        return new ResponseEntity<>(new JSONObject(Map.of("result", "pc removed successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestAddPcMaxWattage(String token, String pcName, int value) {
        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];
        pc.setMaxWattage(value);
        user.scheduleSave();
        ;
        return new ResponseEntity<>(new JSONObject(Map.of("result", "Max wattage set successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestAddPcBatteryCapacity(String token, String pcName, int value) {

        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var user = (User) ((Object[]) result)[0];
        var pc = (Pc) ((Object[]) result)[1];

        if (value > 1000000 || value < 100)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "the value is invalid!")), HttpStatus.OK);
        pc.setBatteryCapacityMw(value);
        user.scheduleSave();
        return new ResponseEntity<>(new JSONObject(Map.of("result", "Battery capacity set successfully!")), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestCalculateWattageMean(String token, String pcName, String startDate,
                                                              String endDate,boolean onlyGpu,boolean onlyBatteryCharge) {
        var start = Controller.stringToLocalDate(startDate);
        var end = Controller.stringToLocalDate(endDate);
        if (MINUTES.between(start, end) < 2)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "startDate and endDate are not valid!")), HttpStatus.OK);

        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var pc = (Pc) ((Object[]) result)[1];
        var mean = pc.calculateWattageMean(start, end,onlyGpu,onlyBatteryCharge,
                false,false,false,false,false);
        var meanRounded = Math.round(mean * 100d) / 100d;
        return new ResponseEntity<>(new JSONObject(Map.of("result", "Wattage mean calculated successfully!",
                "value", meanRounded)), HttpStatus.OK);

    }

    public ResponseEntity<Object> requestCalculateWattHour(String token, String pcName, String startDate, String endDate,
                                                           boolean estimateEmpty,boolean onlyGpu,boolean onlyBatteryCharge) {
        var start = Controller.stringToLocalDate(startDate);
        var end = Controller.stringToLocalDate(endDate);
        if (MINUTES.between(start, end) < 2)
            return new ResponseEntity<>(new JSONObject(Map.of("result", "startDate and endDate are not valid!")), HttpStatus.OK);

        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var pc = (Pc) ((Object[]) result)[1];
        var wattHour = pc.calculateWattHour(start, end, estimateEmpty,onlyGpu,onlyBatteryCharge);
        var meanHourRounded = Math.round(wattHour * 100d) / 100d;
        return new ResponseEntity<>(new JSONObject(Map.of("result", "watt hour calculated successfully!",
                "value", meanHourRounded)), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestRequestTodayWattage(String token, String pcName, int intervals,
                                                             String endDate, int durationSeconds, boolean onlyGpu,
                                                             boolean onlyBatteryCharge) {
        var end=Controller.stringFullToLocalDate(endDate);
        var start = end.minusSeconds(durationSeconds);

        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var pc = (Pc) ((Object[]) result)[1];

        var watts = pc.requestWattageData(start, end, intervals,onlyGpu,onlyBatteryCharge
                ,false,false,false,false,false);
        var cpus = pc.requestWattageData(start, end, intervals,false,onlyBatteryCharge
                ,true,false,false,false,false);
        var gpus = pc.requestWattageData(start, end, intervals,false,onlyBatteryCharge
                ,false,true,false,false,false);
        var rams = pc.requestWattageData(start, end, intervals,false,onlyBatteryCharge
                ,false,false,true,false,false);
        var disks = pc.requestWattageData(start, end, intervals,false,onlyBatteryCharge
                ,false,false,false,true,false);
        var temps = pc.requestWattageData(start, end, intervals,false,onlyBatteryCharge
                ,false,false,false,false,true);

        var wattMean=pc.calculateWattageMean(start,end,onlyGpu,onlyBatteryCharge,
                false,false,false,false,false);
        var cpuMean=pc.calculateWattageMean(start,end,false,onlyBatteryCharge,
                true,false,false,false,false);
        var gpuMean=pc.calculateWattageMean(start,end,false,onlyBatteryCharge,
                false,true,false,false,false);
        var ramMean=pc.calculateWattageMean(start,end,false,onlyBatteryCharge,
                false,false,true,false,false);
        var diskMean=pc.calculateWattageMean(start,end,false,onlyBatteryCharge,
                false,false,false,true,false);
        var tempMean=pc.calculateWattageMean(start,end,false,onlyBatteryCharge,
                false,false,false,false,true);

        var wattHour = pc.calculateWattHour(start, end, false,onlyGpu,onlyBatteryCharge);
        var wattHourEstimated = pc.calculateWattHour(start, end, true,onlyGpu,onlyBatteryCharge);

        var map=new HashMap<>(){{
            put("result", "data collected successfully!");
            put("watts", watts);
            put("cpus", cpus);
            put("gpus", gpus);
            put("rams", rams);
            put("disks", disks);
            put("temps", temps);
            put("wattMean", wattMean);
            put("cpuMean", cpuMean);
            put("gpuMean", gpuMean);
            put("ramMean", ramMean);
            put("diskMean", diskMean);
            put("tempMean", tempMean);
            put("wattHour", wattHour);
            put("wattHourEstimated",wattHourEstimated);
        }};

        return new ResponseEntity<>(new JSONObject(map), HttpStatus.OK);
    }

    public ResponseEntity<Object> requestGenerateRandomWattageData(String token, String pcName, int interval) {
        var now = LocalDateTime.now();
        var start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        var end = start.plusDays(1);

        var result = validateUserAndPc(token, pcName);
        if (result.getClass() == ResponseEntity.class)
            return (ResponseEntity<Object>) result;
        var pc = (Pc) ((Object[]) result)[1];

        pc.generateRandomWattageData(start, end, interval);

        return new ResponseEntity<>(new JSONObject(Map.of("result", "random data generated successfully!")), HttpStatus.OK);

    }
}

