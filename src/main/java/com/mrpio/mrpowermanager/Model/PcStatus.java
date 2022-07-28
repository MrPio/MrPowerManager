package com.mrpio.mrpowermanager.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PcStatus implements Serializable {
    boolean wifi, bluetooth, batteryPlugged,airplane,mute,redLight,saveBattery,hotspot;
    int sound, brightness, batteryPerc, batteryMinutes, cpuLevel, ramLevel,redLightLevel;
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
                    @JsonProperty("ramLevel")int ramLevel,
                    @JsonProperty("airplane")boolean airplane,
                    @JsonProperty("mute")boolean mute,
                    @JsonProperty("redLight")boolean redLight,
                    @JsonProperty("saveBattery")boolean saveBattery,
                    @JsonProperty("hotspot")boolean hotspot,
                    @JsonProperty("redLightLevel")int redLightLevel) {
        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.batteryPlugged = batteryPlugged;
        this.sound = sound;
        this.brightness = brightness;
        this.batteryPerc = batteryPerc;
        this.batteryMinutes = batteryMinutes;
        this.cpuLevel = cpuLevel;
        this.ramLevel = ramLevel;
        this.airplane=airplane;
        this.mute=mute;
        this.redLight=redLight;
        this.saveBattery=saveBattery;
        this.hotspot=hotspot;
        this.redLightLevel=redLightLevel;
        updated=LocalDateTime.now();
    }

    public boolean isAirplane() {
        return airplane;
    }

    public boolean isMute() {
        return mute;
    }

    public boolean isRedLight() {
        return redLight;
    }

    public boolean isSaveBattery() {
        return saveBattery;
    }

    public boolean isHotspot() {
        return hotspot;
    }

    public int getRedLightLevel() {
        return redLightLevel;
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
