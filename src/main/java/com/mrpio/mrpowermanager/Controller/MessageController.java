package com.mrpio.mrpowermanager.Controller;

import com.mrpio.mrpowermanager.Model.Command;
import com.mrpio.mrpowermanager.Model.PcStatus;
import com.mrpio.mrpowermanager.Model.UserStorage;
import com.mrpio.mrpowermanager.Model.WattageEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MessageController {

    final String ENDPOINT_SCHEDULE_COMMAND = "/scheduleCommand/{token}/{pcName}";
    final String ENDPOINT_UPDATE_PC_STATUS = "/updatePcStatus/{token}/{pcName}";
    final String ENDPOINT_SET_ONLINE = "/setOnline/{token}/{pcName}";
    final String ENDPOINT_SET_CLIENT_ONLINE = "/setOnline/{token}";
    final String ENDPOINT_SEND_MESSAGE_TO_CLIENT = "/sendMessage/to/client/{token}/{pcName}";
    final String ENDPOINT_SEND_MESSAGE_TO_SERVER = "/sendMessage/to/server/{token}/{pcName}";

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    //for debug purposes
    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, String message) {
        System.out.println("handling send message: " + "/topic/messages/" + to);
        boolean isExists = UserStorage.getInstance().getUsers().contains(to);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + to, message);
    }

    @MessageMapping(ENDPOINT_SCHEDULE_COMMAND)
    public void scheduleCommand(
            @DestinationVariable String token,
            @DestinationVariable String pcName,
            String args) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
//        System.out.println("/scheduleCommand/"+Controller.keepOnlyAlphaNum(token)+"/"+Controller.keepOnlyAlphaNum(pcName)+
//                "\n"+ Arrays.toString(args.split("~")));
        var newToken = Controller.keepOnlyAlphaNum(token);
        var newPcName = Controller.keepOnlyAlphaNum(pcName);
        var constructor = Command.class.getConstructor(String.class, String.class, String.class);
        Command command;
        if (args.split("~").length == 2)
            command = constructor.newInstance(args.split("~")[0], args.split("~")[1], "null");
        else
            command = constructor.newInstance((Object[]) args.split("~"));

        simpMessagingTemplate.convertAndSend("/server/" + newToken + "/" + newPcName + "/commands", command);
    }

    @MessageMapping(ENDPOINT_UPDATE_PC_STATUS)
    public void updatePcStatus(
            @DestinationVariable String token,
            @DestinationVariable String pcName,
            String args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
//        System.out.println("/updatePcStatus/"+Controller.keepOnlyAlphaNum(token)+"/"+Controller.keepOnlyAlphaNum(pcName)+
//                "\n"+ Arrays.toString(args.split("~")));
        var newToken = Controller.keepOnlyAlphaNum(token);
        var newPcName = Controller.keepOnlyAlphaNum(pcName);
        var constructor = Arrays.stream(PcStatus.class.getConstructors()).filter
                        ((c) -> c.getParameterTypes().length > 1 && c.getParameterTypes()[1] == String.class)
                .collect(Collectors.toList()).get(0);
        var pcStatus = (PcStatus) constructor.newInstance((Object[]) args.split("~"));
        //setMaxWattage
        for(var user: Controller.usersCache)
            if(user.getToken().equals(token) && user.getPc(pcName)!=null)
                pcStatus.setWattage(
                        new WattageEntry(LocalDateTime.now(ZoneOffset.UTC),pcStatus.isBatteryPlugged(),
                                pcStatus.getCpuLevel(),pcStatus.getGpuLevel(),pcStatus.getRamLevel(),
                                pcStatus.getStorageLevel(),pcStatus.getGpuTemp(),
                                pcStatus.getBatteryPerc(),pcStatus.getBatteryChargeRate(),
                                pcStatus.getBatteryDischargeRate())
                                .calculateWattage(user.getPc(pcName).getMaxWattage())
                );

        simpMessagingTemplate.convertAndSend("/client/" + newToken + "/" + newPcName + "/status", pcStatus);
    }

    @MessageMapping(ENDPOINT_SET_ONLINE)
    public void setOnline(
            @DestinationVariable String token,
            @DestinationVariable String pcName,
            String arg) {
        var newToken = Controller.keepOnlyAlphaNum(token);
        var map=Map.of("pcName",pcName,"online",Boolean.valueOf(arg));
        simpMessagingTemplate.convertAndSend("/client/" + newToken + "/online", map);
    }

    @MessageMapping(ENDPOINT_SET_CLIENT_ONLINE)
    public void setClientOnline(
            @DestinationVariable String token,
            String arg) {
        var newToken = Controller.keepOnlyAlphaNum(token);
        var map=Map.of("online",Boolean.valueOf(arg));
        simpMessagingTemplate.convertAndSend("/server/" + newToken + "/online", map);
    }

    @MessageMapping(ENDPOINT_SEND_MESSAGE_TO_CLIENT)
    public void sendMessageToClient(
            @DestinationVariable String token,
            @DestinationVariable String pcName,
            String message) {
        var newToken = Controller.keepOnlyAlphaNum(token);
        var map=Map.of("pcName",pcName,"message",message);
        simpMessagingTemplate.convertAndSend("/client/" + newToken + "/message", map);
    }

    @MessageMapping(ENDPOINT_SEND_MESSAGE_TO_SERVER)
    public void sendMessageToServer(
            @DestinationVariable String token,
            @DestinationVariable String pcName,
            String message) {
        var newToken = Controller.keepOnlyAlphaNum(token);
        var newPcName = Controller.keepOnlyAlphaNum(pcName);
        var map=Map.of("message",message);
        simpMessagingTemplate.convertAndSend("/server/" + newToken + "/"+newPcName+"/message", map);
    }


    @MessageMapping("/keepAlive")
    public void blastToClientsHostReport() {
//        System.out.println("Keep alive received");
    }
}
