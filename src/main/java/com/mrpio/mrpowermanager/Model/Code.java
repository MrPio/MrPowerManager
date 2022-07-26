package com.mrpio.mrpowermanager.Model;

import com.mrpio.mrpowermanager.Service.Serialization;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Code implements Serializable {
    public final static String DIR = "database/";
    private LocalDateTime generatedDate;
    private String code;
    private String token;
    private String pcName;

    public Code(String code, String token, String pcName) {
        this.code = code;
        this.token = token;
        this.generatedDate = LocalDateTime.now();
        this.pcName = pcName;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public String getCode() {
        return code;
    }

    public String getToken() {
        return token;
    }

    public String getPcName() {
        return pcName;
    }

    public static String generateCode(String token, String pcName) {
        var serialization = new Serialization(DIR, "codes.dat");
        ArrayList<Code> codes = new ArrayList<>();
        if (serialization.existFile())
            codes = (ArrayList<Code>) serialization.loadObject();

        //rimuovo vecchi codici
        codes.removeIf(code -> Math.abs(MINUTES.between(code.getGeneratedDate(), LocalDateTime.now())) > 5);

        String code = String.valueOf((new Random().nextInt(1000000)));
        codes.add(new Code(code, token, pcName));
        serialization.saveObject(codes);
        return code;
    }

    public static ResponseEntity<Object> validateCode(String code) {
        var serialization = new Serialization(DIR, "codes.dat");
        ArrayList<Code> codes = new ArrayList<>();
        if (serialization.existFile())
            codes = (ArrayList<Code>) serialization.loadObject();

        for (var c : codes) {
            if (c.getCode().equals(code)) {
                if (Math.abs(MINUTES.between(c.getGeneratedDate(), LocalDateTime.now())) > 4) {
                    codes.remove(c);
                    serialization.saveObject(codes);
                    return new ResponseEntity<>(new JSONObject(Map.of("result", "this code has expired!")), HttpStatus.OK);
                } else {
                    var user = User.load(c.getToken());
                    var count = 0;
                    if (!user.addPc(c.getPcName()))
                        while (!user.addPc(c.getPcName() + ++count)) ;
                    user.save();
                    codes.remove(c);
                    serialization.saveObject(codes);
                    var pcName=count==0?c.getPcName():c.getPcName()+count;
                    return new ResponseEntity<>(new JSONObject(Map.of("result", "code validated successfully!","pcName",pcName,"token",c.getToken())), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(new JSONObject(Map.of("result", "code not found")), HttpStatus.OK);
    }
}
