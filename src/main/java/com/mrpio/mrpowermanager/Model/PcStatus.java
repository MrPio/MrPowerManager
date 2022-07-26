package com.mrpio.mrpowermanager.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PcStatus implements Serializable {
    boolean wifi, bluetooth, batteryPlugged;
    int sound, brightness, batteryPerc, batteryMinutes, cpuLevel, ramLevel;
    LocalDateTime updated;

    public PcStatus() {
        updated=LocalDateTime.now();
    }

    public PcStatus(@JsonProperty("wifi") boolean wifi,
                    @JsonProperty("bluetooth")boolean bluetooth,
                    @JsonProperty("batteryPlugged")boolean batteryPlugged,
                    @JsonProperty("sound")int sound,
                    @JsonProperty("brightness")int brightness,
                    @JsonProperty("batteryPerc")int batteryPerc,
                    @JsonProperty("batteryMinutes")int batteryMinutes,
                    @JsonProperty("cpuLevel")int cpuLevel,
                    @JsonProperty("ramLevel")int ramLevel) {
        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.batteryPlugged = batteryPlugged;
        this.sound = sound;
        this.brightness = brightness;
        this.batteryPerc = batteryPerc;
        this.batteryMinutes = batteryMinutes;
        this.cpuLevel = cpuLevel;
        this.ramLevel = ramLevel;
        updated=LocalDateTime.now();
    }

    public boolean isWifi() {
        return wifi;
    }

    public boolean isBluetooth() {
        return bluetooth;
    }

    public boolean isBatteryPlugged() {
        return batteryPlugged;
    }

    public int getSound() {
        return sound;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getBatteryPerc() {
        return batteryPerc;
    }

    public int getBatteryMinutes() {
        return batteryMinutes;
    }

    public int getCpuLevel() {
        return cpuLevel;
    }

    public int getRamLevel() {
        return ramLevel;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }
}
