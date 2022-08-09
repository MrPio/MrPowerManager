package com.mrpio.mrpowermanager.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mrpio.mrpowermanager.Controller.Controller;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Command implements Serializable {
    public enum Commands {
        SOUND_VALUE,
        BRIGHTNESS_VALUE,
        BRIGHTNESS_UP,
        BRIGHTNESS_DOWN,

        RED_LIGHT_ON,
        RED_LIGHT_OFF,
        RED_LIGHT_VALUE,

        SLEEP,
        HIBERNATE,
        SHUTDOWN,
        LOCK,
        UNLOCK,
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

        public Commands setValue(int value) {
            this.value = value;return this;
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

    public Command(
            @JsonProperty("command")String command,
            @JsonProperty("value")String value,
            @JsonProperty("commandScheduledDate") String commandScheduledDate) {
        this.command = Commands.valueOf(command).setValue(Integer.parseInt(value));
        this.commandScheduledDate = Controller.stringToLocalDate(commandScheduledDate);
    }

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

    @Override
    public String toString() {
        return "Command{" +
                "command=" + command +
                ", id=" + id +
                ", commandSentDate=" + commandSentDate +
                ", commandScheduledDate=" + commandScheduledDate +
                ", commandReceivedDate=" + commandReceivedDate +
                ", commandDoneDate=" + commandDoneDate +
                ", done=" + done +
                '}';
    }
}
