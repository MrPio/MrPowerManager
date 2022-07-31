package com.mrpio.mrpowermanager.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static java.time.temporal.ChronoUnit.*;

public class Pc implements Serializable {

    public enum State {ONLINE, OFFLINE, PAUSED}
    public enum PasswordType{
        WINDOWS
    }

    private String name;
    private ArrayList<Command> commandList;
    private PcStatus pcStatus;
    private HashMap<PasswordType,String> passwords;
    private HashMap<PasswordType,String> keys;

    public Pc(String name) {
        this.name = name;
        commandList = new ArrayList<>();
        pcStatus = new PcStatus();
        passwords=new HashMap<>();
        keys=new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<PasswordType, String> getPasswords() {
        return passwords;
    }

    public PcStatus getPcStatus() {
        return pcStatus;
    }

    public State getState() {
        return Math.abs(SECONDS.between(pcStatus.updated, LocalDateTime.now())) < 30 ? State.ONLINE : State.OFFLINE;
    }

    public ArrayList<Command> getCommandList() {
        return commandList;
    }

    public String addCommand(Command command) {
        if (command.getCommandScheduledDate().isBefore(command.getCommandSentDate()))
            return "you cannot schedule a command in the past!";
/*
 RIMOSSO PERCHé NON VOGLIO LIMITARE LA QUANTITà DI COMANDI FINO A CHE IL PC NON LI ESEGUE
 for (var c : commandList)
            if (c.getCommand().equals(command.getCommand()) && !c.isDone()
                    && Math.abs(MINUTES.between(c.getCommandScheduledDate(), command.getCommandScheduledDate())) < 5)
                return "another command is scheduled at around this time!";*/

        command.setId(commandList.size());
        commandList.add(command);
        return "command scheduled successfully!";
    }

    public ArrayList<Command> listAvailableCommands() {
        var now = LocalDateTime.now().minusMinutes(10);
        var commands = new ArrayList<Command>();
        var toRemove=new ArrayList<Command>();
        for (var c : commandList) {
            if (!c.isDone() && now.isBefore(c.getCommandScheduledDate())) {
                if (c.getCommandReceivedDate() == null)
                    c.setCommandReceivedDate(now);
                commands.add(c);
            }
            else if(c.isDone()||MINUTES.between(now,c.getCommandScheduledDate())<-15)
                toRemove.add(c);
        }
        commandList.removeAll(toRemove);
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

    public void storePassword(PasswordType passwordType,String password){
        this.passwords.put(passwordType,password);
    }

    public void storeKey(PasswordType passwordType,String key){
        keys.put(passwordType,key);
    }
    public String requestKey(PasswordType passwordType){
        return keys.remove(passwordType);
    }

}
