package com.mrpio.mrpowermanager.Controller;

import com.mrpio.mrpowermanager.Model.PcStatus;
import com.mrpio.mrpowermanager.Service.MainService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.json.simple.JSONObject;

@RestController
public class Controller {
    final String ENDPOINT_SIGNUP = "/signup";
    final String ENDPOINT_LOGIN = "/login";
    final String ENDPOINT_ADD_PC = "/addPc";//<----------------------OBSOLETE
    final String ENDPOINT_SET_PC_STATUS = "/setPcStatus";
    final String ENDPOINT_GET_PC_STATUS = "/getPcStatus";
    final String ENDPOINT_SCHEDULE_SLEEP = "/commands/sleep";
    final String ENDPOINT_AVAILABLE_COMMANDS = "/availableCommands";
    final String ENDPOINT_END_COMMAND = "/endCommand";
    final String ENDPOINT_REQUEST_CODE = "/requestCode";
    final String ENDPOINT_VALIDATE_CODE = "/validateCode";
    final String ENDPOINT_UPDATE_PC_STATUS = "/updatePcStatus";
    final String ENDPOINT_DO_SHUTDOWN = "";
    final String ENDPOINT_SET_RED = "";
    final String ENDPOINT_DO_LOGIN = "";//diversi login
    final String ENDPOINT_DO_SAVE_BATTERY = "";
    final String ENDPOINT_DO_SHOOT = "";

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime stringToLocalDate(String date) {
        return LocalDateTime.parse(date, formatter);
    }

    MainService mainService = new MainService();

    @RequestMapping(path = "/")
    public ResponseEntity<Object> Welcome() {
        return new ResponseEntity<>(
                new JSONObject(Map.of("welcome", "Welcome this is MrPowerManager, RESTful API, restricted for only authorized user" +
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
            @RequestParam(value = "token") String token) {
        return mainService.requestLogin(token);
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

    @RequestMapping(path = ENDPOINT_SET_PC_STATUS, method = RequestMethod.POST)
    public ResponseEntity<Object> requestSetPcStatus(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "value") String value) {
        return mainService.requestSetPcStatus(token, pcName, value);
    }

    @RequestMapping(path = ENDPOINT_SCHEDULE_SLEEP, method = RequestMethod.POST)
    public ResponseEntity<Object> requestScheduleSleep(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pcName") String pcName,
            @RequestParam(value = "scheduleDate", required = false) String scheduleDate) {
        return mainService.requestCommandSleep(token, pcName, scheduleDate);
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

    @RequestMapping(path = "/deleteAll", method = RequestMethod.DELETE)
    public ResponseEntity<Object> requestDeleteAll() throws IOException {
        FileUtils.deleteDirectory(new File("database/"));
        return new ResponseEntity<>(new JSONObject(Map.of("result", "deleted successfully")), HttpStatus.OK);
    }
}
