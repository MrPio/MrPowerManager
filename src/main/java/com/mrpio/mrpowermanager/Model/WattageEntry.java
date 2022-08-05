package com.mrpio.mrpowermanager.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mrpio.mrpowermanager.Controller.Controller;

import java.io.Serializable;
import java.time.LocalDateTime;

public class WattageEntry implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime dateTime;
    private final boolean isPlugged;
    private final int cpuPercentage;
    private final int gpuPercentage;
    private final int ramPercentage;
    private final int diskPercentage;
    private final int temp;
    private final int batteryPercentage;
    private final int batteryChargeRate;
    private final int batteryDischargeRate;

    public WattageEntry(@JsonProperty("dateTime") String dateTime,
                        @JsonProperty("isPlugged") boolean isPlugged,
                        @JsonProperty("cpuPercentage") int cpuPercentage,
                        @JsonProperty("gpuPercentage") int gpuPercentage,
                        @JsonProperty("ramPercentage") int ramPercentage,
                        @JsonProperty("diskPercentage") int diskPercentage,
                        @JsonProperty("temp") int temp,
                        @JsonProperty("batteryPercentage") int batteryPercentage,
                        @JsonProperty("batteryChargeRate") int batteryChargeRate,
                        @JsonProperty("batteryDischargeRate") int batteryDischargeRate) {
        this.dateTime = Controller.stringFullToLocalDate(dateTime);
        this.isPlugged = isPlugged;
        this.cpuPercentage = cpuPercentage;
        this.gpuPercentage = gpuPercentage;
        this.ramPercentage = ramPercentage;
        this.diskPercentage = diskPercentage;
        this.temp = temp;
        this.batteryPercentage = batteryPercentage;
        this.batteryChargeRate = batteryChargeRate;
        this.batteryDischargeRate = batteryDischargeRate;
    }

    public WattageEntry(LocalDateTime dateTime, boolean isPlugged, int cpuPercentage, int gpuPercentage, int ramPercentage, int diskPercentage, int temp, int batteryPercentage, int batteryChargeRate, int batteryDischargeRate) {
        this.dateTime = dateTime;
        this.isPlugged = isPlugged;
        this.cpuPercentage = cpuPercentage;
        this.gpuPercentage = gpuPercentage;
        this.ramPercentage = ramPercentage;
        this.diskPercentage = diskPercentage;
        this.temp = temp;
        this.batteryPercentage = batteryPercentage;
        this.batteryChargeRate = batteryChargeRate;
        this.batteryDischargeRate = batteryDischargeRate;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isPlugged() {
        return isPlugged;
    }

    public int getCpuPercentage() {
        return cpuPercentage;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public int getGpuPercentage() {
        return gpuPercentage;
    }

    public int getBatteryChargeRate() {
        return batteryChargeRate;
    }

    public int getBatteryDischargeRate() {
        return batteryDischargeRate;
    }

    public int getRamPercentage() {
        return ramPercentage;
    }

    public int getDiskPercentage() {
        return diskPercentage;
    }

    public int getTemp() {
        return temp;
    }

    public int calculateOnlyGpuWattage(int maxWattage){
        return (int) (gpuPercentage*0.002d * maxWattage);
    }

    public int calculateOnlyBatteryCharge(){
        return getBatteryChargeRate()/1000;
    }

    public int calculateWattage(int maxWattage) {
        if(maxWattage==0)
            return 0;
        if(!isPlugged)
            return (int) Math.round(batteryDischargeRate/1000d);

        if (maxWattage >= 500)
            return (int) (maxWattage * 0.4);
        var cpuClipped = Math.max(cpuPercentage, 3);
        var a = 0.039946d * maxWattage + 1.8654d;
        var c = 0.00063243d * maxWattage + 0.27954d;
        var b = maxWattage / 15.333d;
        var s = 0.002d * maxWattage;
        var p = batteryChargeRate/1000d;
        var wattage = a * Math.pow(cpuClipped - 3, c) + b + gpuPercentage * s + p;
        return (int) Math.round(wattage);
    }
}
