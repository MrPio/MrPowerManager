package com.mrpio.mrpowermanager.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

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
        SAVE_BATTERY,

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

        int value = 50;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private final Commands command;
    private int id;
    private LocalDateTime commandSentDate;
    private LocalDateTime commandScheduledDate;
    private LocalDateTime commandReceivedDate;
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
