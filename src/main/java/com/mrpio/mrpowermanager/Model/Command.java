package com.mrpio.mrpowermanager.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Command implements Serializable {
    public enum Commands {
        SOUND_VALUE,
        BRIGHTNESS_VALUE,

        RED_LIGHT_ON,
        RED_LIGHT_OFF,
        RED_LIGHT_VALUE,

        SLEEP,
        HIBERNATE,
        SHUTDOWN,
        LOCK,
        SAVE_BATTERY_ON,
        SAVE_BATTERY_OFF,

        WIFI_ON,
        WIFI_OFF,
        BLUETOOTH_ON,
        BLUETOOTH_OFF,
        AIRPLANE_ON,
        AIRPLANE_OFF,
        HOTSPOT_ON,
        HOTSPOT_OFF,

        NO_SOUND,
        SOUND_DOWN,
        SOUND_UP,
        PLAY_PAUSE,
        TRACK_PREVIOUS,
        TRACK_NEXT,

        SCREENSHOT;

        private int value = 50;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private final Commands command;
    private int id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commandSentDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commandScheduledDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commandReceivedDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commandDoneDate;

    private boolean done;


    public Command(Commands command, LocalDateTime commandSentDate, LocalDateTime commandScheduledDate) {
        this.command = command;
        this.commandSentDate = commandSentDate;
        this.commandScheduledDate = commandScheduledDate;
    }

    public Commands getCommand() {
        return command;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommandValue() {
        return command.getValue();
    }

    public LocalDateTime getCommandSentDate() {
        return commandSentDate;
    }

    public LocalDateTime getCommandScheduledDate() {
        return commandScheduledDate;
    }

    public LocalDateTime getCommandReceivedDate() {
        return commandReceivedDate;
    }

    public LocalDateTime getCommandDoneDate() {
        return commandDoneDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setCommandSentDate(LocalDateTime commandSentDate) {
        this.commandSentDate = commandSentDate;
    }

    public void setCommandScheduledDate(LocalDateTime commandScheduledDate) {
        this.commandScheduledDate = commandScheduledDate;
    }

    public void setCommandReceivedDate(LocalDateTime commandReceivedDate) {
        this.commandReceivedDate = commandReceivedDate;
    }

    public void setCommandDoneDate(LocalDateTime commandDoneDate) {
        this.commandDoneDate = commandDoneDate;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


}
