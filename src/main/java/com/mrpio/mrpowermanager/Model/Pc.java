package com.mrpio.mrpowermanager.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;

public class Pc implements Serializable {
    public enum State {ONLINE, OFFLINE, PAUSED}

    private final String name;
    private final ArrayList<Command> commandList;
    private PcStatus pcStatus;
    private final HashMap<String, String> passwords;
    private final HashMap<String, String> keys;
    private int maxWattage;
    private int batteryStopCharging;
    private final ArrayList<WattageEntry> wattageEntries;

    public Pc(String name) {
        this.name = name;
        commandList = new ArrayList<>();
        pcStatus = new PcStatus();
        passwords = new HashMap<>();
        keys = new HashMap<>();
        maxWattage = 0;
        batteryStopCharging = 100;
        wattageEntries = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getPasswords() {
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

    public int getMaxWattage() {
        return maxWattage;
    }

    public void setMaxWattage(int maxWattage) {
        this.maxWattage = maxWattage;
    }

    public int getBatteryStopCharging() {
        return batteryStopCharging;
    }

    public void setBatteryStopCharging(int batteryStopCharging) {
        this.batteryStopCharging = batteryStopCharging;
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
        var toRemove = new ArrayList<Command>();
        for (var c : commandList) {
            if (!c.isDone() && now.isBefore(c.getCommandScheduledDate())) {
                if (c.getCommandReceivedDate() == null)
                    c.setCommandReceivedDate(now);
                commands.add(c);
            } else if (c.isDone() || MINUTES.between(now, c.getCommandScheduledDate()) < -15)
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
        wattageEntries.add(new WattageEntry(pcStatus.getUpdated(), pcStatus.isBatteryPlugged(),
                pcStatus.getCpuLevel(), pcStatus.getGpuLevel(), pcStatus.getBatteryPerc()));
        this.pcStatus = pcStatus;
    }

    public void uploadWattageEntries(WattageEntry[] wattageEntries) {
        this.wattageEntries.addAll(List.of(wattageEntries));
    }

    public boolean storePassword(String title, String password) {
        var update= passwords.containsKey(title);
        this.passwords.put(title, password);
        return update;
    }

    public String deletePassword(String title) {
        return passwords.remove(title);
    }


    public boolean storeKey(String title, String key) {
        var update= keys.containsKey(title);
        keys.put(title, key);
        return update;
    }

    public String requestKey(String title) {
        return keys.remove(title);
    }

    public double calculateWattageMean(LocalDateTime start, LocalDateTime end) {
        //It must be sorted by data
        double weightedSum = 0;
        double weight = 0;
        WattageEntry lastWatt = null;
        for (var watt : wattageEntries) {
            if (lastWatt == null) {
                lastWatt = watt;
                continue;
            }
            if (watt.getDateTime().isBefore(end) && watt.getDateTime().isAfter(start)) {
                var millisBetween = Math.abs(MILLIS.between(watt.getDateTime(), lastWatt.getDateTime()));
                if (millisBetween < 120 * 1000) {
                    weightedSum += millisBetween * watt.calculateWattage(maxWattage,batteryStopCharging);
                    weight += millisBetween;
                }
            }
            lastWatt = watt;
        }
        return weightedSum / weight;
    }

    public double calculateWattHour(LocalDateTime start, LocalDateTime end, boolean alsoEstimateEmptyZones) {
        double weightedSum = 0;
        WattageEntry lastWatt = null;
        for (var watt : wattageEntries) {
            if (lastWatt == null) {
                lastWatt = watt;
                continue;
            }
            if (watt.getDateTime().isBefore(end) && watt.getDateTime().isAfter(start)) {
                var millisBetween = Math.abs(MILLIS.between(watt.getDateTime(), lastWatt.getDateTime()));
                if (millisBetween < 120 * 1000)
                    weightedSum += millisBetween * watt.calculateWattage(maxWattage,batteryStopCharging);
                else if (alsoEstimateEmptyZones)
                    weightedSum += millisBetween * calculateWattageMean(lastWatt.getDateTime().minusMinutes(10), watt.getDateTime().plusMinutes(10));
            }
            lastWatt = watt;
        }
        return weightedSum / 3600 / 1000;
    }


    public ArrayList<WattageEntry> getWattageEntries() {
        return wattageEntries;
    }
}
