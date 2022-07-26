package com.mrpio.mrpowermanager.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;

public class Pc implements Serializable {

    public enum State {ONLINE, OFFLINE, PAUSED}

    private final String name;
    private final ArrayList<Command> commandList;
    private PcStatus pcStatus;
    private final ArrayList<Login> logins;
    private final HashMap<String, String> keys;
    private int maxWattage;
    private final ArrayList<WattageEntry> wattageEntries;
    private final ArrayList<WattageEntry> oldWattageEntries;
    private int batteryCapacityMw;

    public Pc(String name) {
        this.name = name;
        commandList = new ArrayList<>();
        pcStatus = new PcStatus();
        logins = new ArrayList<>();
        keys = new HashMap<>();
        maxWattage = 0;
        batteryCapacityMw = 0;
        wattageEntries = new ArrayList<>();
        oldWattageEntries = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Login> getLogins() {
        return logins;
    }

    public PcStatus getPcStatus() {
        return pcStatus;
    }

    public State getState() {
        return Math.abs(SECONDS.between(pcStatus.updated, LocalDateTime.now(ZoneOffset.UTC))) < 30 ? State.ONLINE : State.OFFLINE;
    }

//    public ArrayList<Command> getCommandList() {
//        return commandList;
//    }

    public int getMaxWattage() {
        return maxWattage;
    }

    public void setMaxWattage(int maxWattage) {
        this.maxWattage = maxWattage;
    }


    public int getBatteryCapacityMw() {
        return batteryCapacityMw;
    }

    public double getWattage() {
        if (wattageEntries.isEmpty())
            return 0;
        return wattageEntries.get(wattageEntries.size() - 1).calculateWattage(maxWattage);
    }

    public double getOnlyGpuWattage() {
        if (wattageEntries.isEmpty())
            return 0;
        return wattageEntries.get(wattageEntries.size() - 1).calculateOnlyGpuWattage(maxWattage);
    }

    public double getBatteryCharging() {
        if (wattageEntries.isEmpty())
            return 0;
        return wattageEntries.get(wattageEntries.size() - 1).getBatteryChargeRate();
    }

    public double getBatteryDischarging() {
        if (wattageEntries.isEmpty())
            return 0;
        return wattageEntries.get(wattageEntries.size() - 1).getBatteryDischargeRate();
    }

    public void setBatteryCapacityMw(int batteryCapacityMw) {
        this.batteryCapacityMw = batteryCapacityMw;
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

    /**
     * Passa tutti i comandi con la flag isDone falsa, non c'è alcun controllo sulla data di schedule per motivi di Fuso orario
     * ogni comando che restituisco nel JSON di risposta lo segno come eseguito, lasciando al server il compito di gestire lo schedule.
     */
    public ArrayList<Command> listAvailableCommands() {
//        var now = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(10);
        var commands = new ArrayList<Command>();
        var toRemove = new ArrayList<Command>();
        for (var c : commandList) {
            if (!c.isDone() /*&& now.isBefore(c.getCommandScheduledDate())*/) {
                if (c.getCommandReceivedDate() == null)
                    c.setCommandReceivedDate(LocalDateTime.now(ZoneOffset.UTC));
                commands.add(c);
                c.setDone(true);
            } else {
                toRemove.add(c);
            }
/*             else if (c.isDone() || MINUTES.between(now, c.getCommandScheduledDate()) < -15)
                toRemove.add(c);*/
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
                    c.setCommandDoneDate(LocalDateTime.now(ZoneOffset.UTC));
                    c.setDone(true);
                    return "command ended successfully!";
                }
            }
        return "id not found!";
    }

    public void updatePcStatus(PcStatus pcStatus) {
        wattageEntries.add(new WattageEntry(pcStatus.getUpdated(), pcStatus.isBatteryPlugged(),
                pcStatus.getCpuLevel(), pcStatus.getGpuLevel(), pcStatus.getRamLevel(),
                pcStatus.getStorageLevel(), pcStatus.getGpuTemp(), pcStatus.getBatteryPerc(),
                pcStatus.getBatteryChargeRate(), pcStatus.getBatteryDischargeRate()));
        this.pcStatus = pcStatus;
    }

    public void uploadWattageEntries(WattageEntry[] wattageEntries) {
        this.wattageEntries.addAll(List.of(wattageEntries));
    }

    public boolean deleteLogin(String title) {
        return logins.removeIf(login -> login.getTitle().equals(title));
    }

    public boolean storeLogin(String title, String url,String username, String password,String args){
        var update=logins.removeIf(login -> login.getTitle().equals(title));
        this.logins.add(new Login(title,url,username,password,args));
        return update;
    }


    public boolean storeKey(String title, String key) {
        var update = keys.containsKey(title);
        keys.put(title, key);
        return update;
    }

    public String requestKey(String title) {
        return keys.remove(title);
    }

    public double calculateWattageMean(LocalDateTime start, LocalDateTime end, boolean onlyGpu,
                                       boolean onlyBatteryCharge,
                                       boolean cpu, boolean gpu, boolean ram, boolean disk, boolean temp, boolean... force) {
        //It must be sorted by data
        var span = force.length == 1 && force[0];

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
                if (span || millisBetween < 120 * 1000) {
                    if (onlyGpu)
                        weightedSum += millisBetween * watt.calculateOnlyGpuWattage(maxWattage);
                    else if (onlyBatteryCharge)
                        weightedSum += millisBetween * watt.calculateOnlyBatteryCharge();
                    else if (cpu)
                        weightedSum += millisBetween * watt.getCpuPercentage();
                    else if (gpu)
                        weightedSum += millisBetween * watt.getGpuPercentage();
                    else if (ram)
                        weightedSum += millisBetween * watt.getRamPercentage();
                    else if (disk)
                        weightedSum += millisBetween * watt.getDiskPercentage();
                    else if (temp)
                        weightedSum += millisBetween * watt.getTemp();
                    else
                        weightedSum += millisBetween * watt.calculateWattage(maxWattage);
                    weight += millisBetween;
                }
            }
            lastWatt = watt;
        }
        return Math.round((weightedSum / weight) * 100d) / 100d;
    }

    public double calculateWattHour(LocalDateTime start, LocalDateTime end, boolean alsoEstimateEmptyZones,
                                    boolean onlyGpu, boolean onlyBatteryCharge, boolean... force) {
        var span = force.length == 1 && force[0];

        double weightedSum = 0;
        WattageEntry lastWatt = null;
        for (var watt : wattageEntries) {
            if (lastWatt == null) {
                lastWatt = watt;
                continue;
            }
            if (lastWatt.getDateTime().isBefore(start)) {
                lastWatt = watt;
                continue;
            }
            if (watt.getDateTime().isBefore(end) && watt.getDateTime().isAfter(start)) {
                var millisBetween = Math.abs(MILLIS.between(watt.getDateTime(), lastWatt.getDateTime()));
                if (span || millisBetween < 120 * 1000)
                    if (onlyGpu)
                        weightedSum += millisBetween * watt.calculateOnlyGpuWattage(maxWattage);
                    else if (onlyBatteryCharge)
                        weightedSum += millisBetween * watt.calculateOnlyBatteryCharge();
                    else
                        weightedSum += millisBetween * watt.calculateWattage(maxWattage);
                else if (alsoEstimateEmptyZones)
                    weightedSum += millisBetween * calculateWattageMean
                            (lastWatt.getDateTime().minusMinutes(10), watt.getDateTime().plusMinutes(10),
                                    onlyGpu, onlyBatteryCharge, false, false, false, false, false);
            }
            lastWatt = watt;
        }
        return Math.round((weightedSum / 3600 / 1000) * 100d) / 100d;
    }

    public ArrayList<Double> requestWattageData(LocalDateTime start, LocalDateTime end, int intervals,
                                                boolean onlyGpu, boolean onlyBatteryCharge,
                                                boolean cpu, boolean gpu, boolean ram, boolean disk, boolean temp) {
        var data = new ArrayList<Double>();
        var seconds = Math.max(16, SECONDS.between(start, end)) / intervals;

        var start2 = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(),
                start.getHour(), start.getMinute(), start.getSecond());
        for (int i = 0; i < intervals; ++i) {
            start2 = start2.plusSeconds(seconds);
//            if (seconds <= 30) {
            data.add(0d);
            for (var watt : wattageEntries) {
                if (watt.getDateTime().isAfter(start) && watt.getDateTime().isBefore(start2)) {
                    data.remove(data.size() - 1);
                    if (onlyGpu)
                        data.add((double) watt.calculateOnlyGpuWattage(maxWattage));
                    else if (onlyBatteryCharge)
                        data.add((double) watt.calculateOnlyBatteryCharge());
                    else if (cpu)
                        data.add((double) watt.getCpuPercentage());
                    else if (gpu)
                        data.add((double) watt.getGpuPercentage());
                    else if (ram)
                        data.add((double) watt.getRamPercentage());
                    else if (disk)
                        data.add((double) watt.getDiskPercentage());
                    else if (temp)
                        data.add((double) watt.getTemp());
                    else
                        data.add((double) watt.calculateWattage(maxWattage));
                    break;
                }
            }
//            } else
//                data.add(Math.round(calculateWattageMean(start, start2, onlyGpu, onlyBatteryCharge, cpu, gpu, ram, disk,temp) * 100d) / 100d);
            start = start.plusSeconds(seconds);
        }
        return data;
    }

    //just for debug
    public void generateRandomWattageData(LocalDateTime start, LocalDateTime end, int interval) {
        //only today
        var steps = 3600 * 24 / interval;
        for (int i = 0; i < steps; ++i) {
            start = start.plusSeconds(interval);
            wattageEntries.add(new WattageEntry(start, true, (int) (Math.random() * 101),
                    (int) (Math.random() * 101), (int) (Math.random() * 101), (int) (Math.random() * 101)
                    , (int) (Math.random() * 101), (int) (Math.random() * 101), 0, 0));
        }
    }


    public void cleanWattageEntries() {
        var minDate = LocalDateTime.now(ZoneOffset.UTC).minusDays(2);
        var toMove = new ArrayList<WattageEntry>();
        for (var wattage : wattageEntries)
            if (wattage.getDateTime().isBefore(minDate))
                toMove.add(wattage);
        wattageEntries.removeAll(toMove);
        var lastDate = oldWattageEntries.isEmpty() ? minDate.minusYears(10) :
                oldWattageEntries.get(oldWattageEntries.size() - 1).getDateTime();
        var toAdd = new ArrayList<WattageEntry>();
        for (var wattage : toMove) {
            if (MINUTES.between(lastDate, wattage.getDateTime()) >= 24) {
                toAdd.add(wattage);
                lastDate = wattage.getDateTime();
            }
        }
        oldWattageEntries.addAll(toAdd);
    }

/*    public ArrayList<WattageEntry> getWattageEntries() {
        return wattageEntries;
    }*/
}
