package com.mrpio.mrpowermanager.Controller;

import com.mrpio.mrpowermanager.Model.*;
import com.mrpio.mrpowermanager.Service.MainService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {
    public static List<User> usersCache = new ArrayList<>();


    final String ENDPOINT_SIGNUP = "/signup";
    final String ENDPOINT_LOGIN = "/login";
    final String ENDPOINT_ADD_PC = "/addPc";//--------------------------------------------DEPRECATED
    final String ENDPOINT_GET_PC_STATUS = "/getPcStatus";//TODO<--------------------------WEBSOCKET
    final String ENDPOINT_SCHEDULE_COMMAND = "/scheduleCommand";//------------------------restored
    final String ENDPOINT_AVAILABLE_COMMANDS = "/availableCommands";//--------------------restored
    final String ENDPOINT_END_COMMAND = "/endCommand";//----------------------------------DEPRECATED
    final String ENDPOINT_REQUEST_CODE = "/requestCode";
    final String ENDPOINT_VALIDATE_CODE = "/validateCode";//TODO<-------------------------WEBSOCKET
    final String ENDPOINT_UPDATE_PC_STATUS = "/updatePcStatus";//<------------------------WEBSOCKET
    final String ENDPOINT_UPLOAD_WATTAGE_ENTRIES = "/uploadWattageEntries";
    final String ENDPOINT_STORE_PASSWORD = "/storePassword";
    final String ENDPOINT_DELETE_PASSWORD = "/deletePassword";
    final String ENDPOINT_SEND_KEY = "/sendKey";//TODO<-----------------------------------WEBSOCKET
    final String ENDPOINT_REQUEST_KEY = "/requestKey";//TODO<-----------------------------WEBSOCKET
    final String ENDPOINT_DELETE_ACCOUNT = "/deleteAccount";
    final String ENDPOINT_DELETE_PC = "/deletePc";
    final String ENDPOINT_ADD_PC_MAX_WATTAGE = "/addPcMaxWattage";//given by client
    final String ENDPOINT_ADD_PC_BATTERY_CAPACITY = "/addPcBatteryCapacity";//given by server
    final String ENDPOINT_CALCULATE_WATTAGE_MEAN = "/calculateWattageMean";
    final String ENDPOINT_CALCULATE_WATT_HOUR = "/calculateWattHour";
    final String ENDPOINT_REQUEST_TODAY_WATTAGE = "/requestTodayWattage";
    final String ENDPOINT_GENERATE_RANDOM_WATTAGE_DATA = "/generateRandomWattageData";

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    static DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime stringToLocalDate(String date) {
        return LocalDateTime.parse(date, formatter);
    }

    public static LocalDateTime stringFullToLocalDate(String date) {
        return LocalDateTime.parse(date, formatterFull);
    }

    public static String keepOnlyAlphaNum(String raw){
        return raw.replaceAll("[^a-zA-Z0-9]+","_");
    }

    MainService mainService = new MainService();

    @RequestMapping(path = "/")
    public ResponseEntity<Object> Welcome() {
        return new ResponseEntity<>(
                new JSONObject(Map.of("welcome", "Welcome this is MrPowerManager, REST-full API, restricted for only authorized user" +
                        "you can find the repository here: " +
                        "https://github.com/MrPio/MrPowerManager")),
                HttpStatus.OK);
    }

    @RequestMapping(path = ENDPOINT_SIGNUP, method = RequestMethod.POST)
    public ResponseEntity<Object> requestSignUp(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "email") String email) {
        return mainService.requestSignUp(token, email);
    }

    @RequestMapping(path = ENDPOINT_LOGIN, method = RequestMethod.GET)
    public ResponseEntity<Object> requestLogin(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "imTheClient", defaultValue = "false") boolean imTheClient) {
        if (imTheClient){
            var newToken = Controller.keepOnlyAlphaNum(token);
            var map=Map.of("online",true);
            simpMessagingTemplate.convertAndSend("/server/" + newToken + "/online", map);
        }
        return mainService.requestLogin(token, imTheClient);
    }

    @RequestMapping(path = ENDPOINT_ADD_PC, method = RequestMethod.POST)
    public ResponseEntity<Object> requestAddPc(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "name") String name) {
        return mainService.requestAddPc(token, name);
    }

    @RequestMapping(path = ENDPOINT_GET_PC_STATUS, method = RequestMethod.GET)
    public ResponseEntity<Object> requestGetPcStatus(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName) {
        return mainService.requestGetPcStatus(token, pcName);
    }


    @RequestMapping(path = ENDPOINT_SCHEDULE_COMMAND, method = RequestMethod.POST)
    public ResponseEntity<Object> requestScheduleSleep(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "command") String command,
            @RequestParam(value = "value", defaultValue = "50") Integer value,
            @RequestParam(value = "scheduleDate", required = false) String scheduleDate) {
        return mainService.requestScheduleCommand(token, pcName, command,value, scheduleDate);
    }

    @RequestMapping(path = ENDPOINT_AVAILABLE_COMMANDS, method = RequestMethod.GET)
    public ResponseEntity<Object> requestGetAvailableCommands(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName) {
        return mainService.requestAvailableCommands(token, pcName);
    }

    @RequestMapping(path = ENDPOINT_END_COMMAND, method = RequestMethod.POST)
    public ResponseEntity<Object> requestEndCommand(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "id", required = false) Integer id) {
        return mainService.requestEndCommand(token, pcName, id);
    }

    @RequestMapping(path = ENDPOINT_REQUEST_CODE, method = RequestMethod.GET)
    public ResponseEntity<Object> requestCode(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName) {
        return mainService.requestCode(token, pcName);
    }

    @RequestMapping(path = ENDPOINT_VALIDATE_CODE, method = RequestMethod.GET)
    public ResponseEntity<Object> requestValidateCode(
            @RequestParam(value = "code") String code) {
        return mainService.requestValidateCode(code);
    }

    @RequestMapping(path = ENDPOINT_UPDATE_PC_STATUS, method = RequestMethod.POST)
    public ResponseEntity<Object> requestUpdatePcStatus(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestBody PcStatus pcStatus) {
        return mainService.requestUpdatePcStatus(token, pcName, pcStatus);
    }

    @RequestMapping(path = ENDPOINT_UPLOAD_WATTAGE_ENTRIES, method = RequestMethod.POST)
    public ResponseEntity<Object> requestUploadWattageEntries(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestBody WattageEntry[] wattageEntries) {
        return mainService.requestUploadWattageEntries(token, pcName, wattageEntries);
    }

    @RequestMapping(path = "/deleteAll", method = RequestMethod.DELETE)
    public ResponseEntity<Object> requestDeleteAll() throws IOException {
        FileUtils.deleteDirectory(new File("database/"));
        return new ResponseEntity<>(new JSONObject(Map.of("result", "deleted successfully")), HttpStatus.OK);
    }

    @RequestMapping(path = ENDPOINT_STORE_PASSWORD, method = RequestMethod.POST)
    public ResponseEntity<Object> requestStorePassword(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "password") String password) {
        return mainService.requestStorePassword(token, pcName, title, password);
    }

    @RequestMapping(path = ENDPOINT_DELETE_PASSWORD, method = RequestMethod.DELETE)
    public ResponseEntity<Object> requestDeletePassword(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "title") String title) {
        return mainService.requestDeletePassword(token, pcName, title);
    }

    @RequestMapping(path = ENDPOINT_SEND_KEY, method = RequestMethod.POST)
    public ResponseEntity<Object> requestSendKey(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "key") String key) {
        return mainService.requestSendKey(token, pcName, title, key);
    }

    @RequestMapping(path = ENDPOINT_REQUEST_KEY, method = RequestMethod.GET)
    public ResponseEntity<Object> requestRequestKey(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "title") String title) {
        return mainService.requestRequestKey(token, pcName, title);
    }


    @RequestMapping(path = ENDPOINT_DELETE_ACCOUNT, method = RequestMethod.DELETE)
    public ResponseEntity<Object> requestDeleteAccount(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "email") String email) {
        return mainService.requestDeleteAccount(token, email);
    }

    @RequestMapping(path = ENDPOINT_DELETE_PC, method = RequestMethod.DELETE)
    public ResponseEntity<Object> requestDeletePc(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName) {
        return mainService.requestDeletePc(token, pcName);
    }

    @RequestMapping(path = ENDPOINT_ADD_PC_MAX_WATTAGE, method = RequestMethod.POST)
    public ResponseEntity<Object> requestAddPcMaxWattage(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "value") int value) {
        return mainService.requestAddPcMaxWattage(token, pcName, value);
    }

    @RequestMapping(path = ENDPOINT_ADD_PC_BATTERY_CAPACITY, method = RequestMethod.POST)
    public ResponseEntity<Object> requestAddPcBatteryCapacity(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "value") int value) {
        return mainService.requestAddPcBatteryCapacity(token, pcName, value);
    }

    @RequestMapping(path = ENDPOINT_CALCULATE_WATTAGE_MEAN, method = RequestMethod.GET)
    public ResponseEntity<Object> requestCalculateWattageMean(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate,
            @RequestParam(value = "onlyGpu", defaultValue = "false") boolean onlyGpu,
            @RequestParam(value = "onlyBatteryCharge", defaultValue = "false") boolean onlyBatteryCharge) {
        return mainService.requestCalculateWattageMean(token, pcName, startDate, endDate,onlyGpu,onlyBatteryCharge);
    }

    @RequestMapping(path = ENDPOINT_CALCULATE_WATT_HOUR, method = RequestMethod.GET)
    public ResponseEntity<Object> requestCalculateWattHour(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate,
            @RequestParam(value = "estimateEmpty", defaultValue = "false") boolean estimateEmpty,
            @RequestParam(value = "onlyGpu", defaultValue = "false") boolean onlyGpu,
            @RequestParam(value = "onlyBatteryCharge", defaultValue = "false") boolean onlyBatteryCharge) {
        return mainService.requestCalculateWattHour(token, pcName, startDate, endDate, estimateEmpty,onlyGpu,onlyBatteryCharge);
    }


    @RequestMapping(path = ENDPOINT_REQUEST_TODAY_WATTAGE, method = RequestMethod.GET)
    public ResponseEntity<Object> requestRequestTodayWattage(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "intervals") int intervals,
            @RequestParam(value = "endDate") String endDate,
            @RequestParam(value = "durationSeconds",defaultValue = "86400") int durationSeconds,
            @RequestParam(value = "onlyGpu", defaultValue = "false") boolean onlyGpu,
            @RequestParam(value = "onlyBatteryCharge", defaultValue = "false") boolean onlyBatteryCharge) {
        return mainService.requestRequestTodayWattage(token, pcName, intervals,endDate,durationSeconds,onlyGpu,
                onlyBatteryCharge);
    }

    @RequestMapping(path = ENDPOINT_GENERATE_RANDOM_WATTAGE_DATA, method = RequestMethod.POST)
    public ResponseEntity<Object> requestGenerateRandomWattageData(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "interval") int interval) {
        return mainService.requestGenerateRandomWattageData(token, pcName, interval);
    }
}
