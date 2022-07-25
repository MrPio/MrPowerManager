package com.mrpio.mrpowermanager.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public abstract class Command implements Serializable {
    private int id;
    private LocalDateTime commandSentDate;
    private LocalDateTime commandScheduledDate;
    private LocalDateTime commandReceivedDate;
    private LocalDateTime commandDoneDate;
    private boolean done;

    public Command(LocalDateTime commandSentDate, LocalDateTime commandScheduledDate) {
        this.commandSentDate = commandSentDate;
        this.commandScheduledDate = commandScheduledDate;
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
