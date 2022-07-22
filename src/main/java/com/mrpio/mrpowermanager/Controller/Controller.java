package com.mrpio.mrpowermanager.Controller;

import com.mrpio.mrpowermanager.Service.MainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import org.json.simple.JSONObject;

@RestController
public class Controller {
    final String ENDPOINT_SIGNUP = "/signup";
    final String ENDPOINT_LOGIN = "/login";
    final String ENDPOINT_STATUS = "/status";
    final String ENDPOINT_DO_SLEEP = "/do-sleep";
    final String ENDPOINT_DO_SHUTDOWN = "";
    final String ENDPOINT_SET_RED = "";
    final String ENDPOINT_DO_LOGIN = "";//diversi login
    final String ENDPOINT_DO_SAVE_BATTERY = "";
    final String ENDPOINT_DO_SHOOT = "";

    MainService mainService = new MainService();

    @RequestMapping(path = "/")
    public ResponseEntity<Object> Welcome() {
        return new ResponseEntity<>(
                new JSONObject(Map.of("welcome", "Welcome this is MrPowerManager, RESTful API, restricted for only authorized user" +
                        "you can find the repository here: " +
                        "https://github.com/MrPio/MrPowerManager")),
                HttpStatus.OK);
    }

    @RequestMapping(path = ENDPOINT_STATUS, method = RequestMethod.GET)
    public ResponseEntity<Object> requestFilter(
            @RequestParam(value = "token") String token) {
        return mainService.requestStatus(token);
    }

    @RequestMapping(path = ENDPOINT_SIGNUP, method = RequestMethod.POST)
    public ResponseEntity<Object> requestSignUp(
            @RequestParam(value = "token") String token) {
        return mainService.requestSignUp(token);
    }

    @RequestMapping(path = ENDPOINT_LOGIN, method = RequestMethod.POST)
    public ResponseEntity<Object> requestLogin(
            @RequestParam(value = "token") String token) {
        return mainService.requestLogin(token);
    }

    @RequestMapping(path = ENDPOINT_DO_SLEEP, method = RequestMethod.POST)
    public ResponseEntity<Object> requestSleep(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "value") Boolean value) {
        return mainService.requestSleep(token,value);
    }
}
