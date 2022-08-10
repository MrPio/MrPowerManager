package com.mrpio.mrpowermanager.Controller;

import com.mrpio.mrpowermanager.Model.Command;
import com.mrpio.mrpowermanager.Model.Message;
import com.mrpio.mrpowermanager.Model.PcStatus;
import com.mrpio.mrpowermanager.Model.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class MessageController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    //for debug purposes
    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, String message) {
        System.out.println("handling send message: " + "/topic/messages/" + to);
        boolean isExists = UserStorage.getInstance().getUsers().contains(to);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + to, message);
    }

    //SCHEDULE COMMAND
    @MessageMapping("/scheduleCommand/{token}/{pcName}")
    public void scheduleCommand(
            @DestinationVariable String token,
            @DestinationVariable String pcName,
            String args) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
//        System.out.println("/scheduleCommand/"+Controller.keepOnlyAlphaNum(token)+"/"+Controller.keepOnlyAlphaNum(pcName)+
//                "\n"+ Arrays.toString(args.split("~")));
        var newToken=Controller.keepOnlyAlphaNum(token);
        var newPcName=Controller.keepOnlyAlphaNum(pcName);
        var constructor=Command.class.getConstructor(String.class,String.class,String.class);
        Command command;
        if(args.split("~").length==2)
            command=constructor.newInstance(args.split("~")[0],args.split("~")[1],"null");
        else
            command=constructor.newInstance((Object[]) args.split("~"));

        simpMessagingTemplate.convertAndSend("/server/"+newToken+"/"+newPcName+"/commands", command);
    }

    @MessageMapping("/updatePcStatus/{token}/{pcName}")
    public void updatePcStatus(
            @DestinationVariable String token,
            @DestinationVariable String pcName,
            String args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
//        System.out.println("/updatePcStatus/"+Controller.keepOnlyAlphaNum(token)+"/"+Controller.keepOnlyAlphaNum(pcName)+
//                "\n"+ Arrays.toString(args.split("~")));
        var newToken=Controller.keepOnlyAlphaNum(token);
        var newPcName=Controller.keepOnlyAlphaNum(pcName);
        var constructor= Arrays.stream(PcStatus.class.getConstructors()).filter
                        ((c) -> c.getParameterTypes().length > 1 &&c.getParameterTypes()[1]==String.class)
                .collect(Collectors.toList()).get(0);
        var pcStatus= (PcStatus) constructor.newInstance((Object[]) args.split("~"));
        simpMessagingTemplate.convertAndSend("/client/"+newToken+"/"+newPcName+"/status", pcStatus);
    }

    @MessageMapping("/keepAlive")
    public void blastToClientsHostReport() {
//        System.out.println("Keep alive received");
    }
}
