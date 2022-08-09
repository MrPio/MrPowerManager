package com.mrpio.mrpowermanager.Controller;

import com.mrpio.mrpowermanager.Model.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

@EnableScheduling
@RestController
public class MessageController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, String message) {
        System.out.println("handling send message: " + "/topic/messages/" + to);
        boolean isExists = UserStorage.getInstance().getUsers().contains(to);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + to, message);
    }

//    @Scheduled(fixedDelayString = "3")
//    public void blastToClientsHostReport() {
//        System.out.println("Sending something on the websocket");
//        simpMessagingTemplate.convertAndSend("/topic/greeting", "Hello World");
//    }
}
