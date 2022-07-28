package com.mrpio.mrpowermanager.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.*;

public class Pc implements Serializable {

    public enum State {ONLINE, OFFLINE, PAUSED}

    private String name;
    private State state = State.OFFLINE;
    private ArrayList<Command> commandList;
    private PcStatus pcStatus;

    public Pc(String name) {
        this.name = name;
        commandList = new ArrayList<>();
        pcStatus = new PcStatus();
    }

    public String getName() {
        return name;
    }

    public PcStatus getPcStatus() {
        return pcStatus;
    }

    public State getState() {
        return state;
    }

    public ArrayList<Command> getCommandList() {
        return commandList;
    }

    public String addCommand(Command command) {
        if (command.getCommandScheduledDate().isBefore(command.getCommandSentDate()))
            return "you cannot schedule a command in the past!";
/*        for (var c : commandList)
            if (c.getCommand().equals(command.getCommand()) && !c.isDone()
                    && Math.abs(MINUTES.between(c.getCommandScheduledDate(), command.getCommandScheduledDate())) < 5)
                return "another command is scheduled at around this time!";*/

        command.setId(commandList.size());
        commandList.add(command);
        return "command scheduled successfully!";
    }

    public void setState(State state) {
        this.state = state;
    }

    public ArrayList<Command> listAvailableCommands() {
        var now = LocalDateTime.now().minusMinutes(10);
        var commands = new ArrayList<Command>();
        for (var c : commandList) {
            if (!c.isDone() && now.isBefore(c.getCommandScheduledDate())) {
                if (c.getCommandReceivedDate() == null)
                    c.setCommandReceivedDate(now);
                commands.add(c);
            }
        }
        return commands;
    }

    public String endCommand(int id) {
        for (var c : commandList)
            if (c.getId() == id) {
                if (c.isDone())
                    return "this command was already over!";
                else {
                    c.setCommandDoneDate(LocalDateTime.now());
                    c.setDone(true);
                    return "command ended successfully!";
                }
            }
        return "id not found!";
    }

    public void updatePcStatus(PcStatus pcStatus) {
        this.pcStatus = pcStatus;
    }
}
