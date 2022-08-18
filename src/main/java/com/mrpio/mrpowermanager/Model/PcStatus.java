package com.mrpio.mrpowermanager.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mrpio.mrpowermanager.Controller.Controller;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class PcStatus implements Serializable {
    private boolean wifi, bluetooth, batteryPlugged, airplane, mute, redLight, saveBattery, hotspot,isClipboardShared;
    private int sound, brightness, batteryPerc, batteryMinutes, cpuLevel, ramLevel, redLightLevel,
            storageLevel,gpuLevel,gpuTemp,batteryChargeRate,batteryDischargeRate,wattage=0;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime updated;

    public PcStatus() {
        updated = LocalDateTime.now(ZoneOffset.UTC);
    }

    public PcStatus(@JsonProperty("wifi") boolean wifi,
                    @JsonProperty("bluetooth") boolean bluetooth,
                    @JsonProperty("batteryPlugged") boolean batteryPlugged,
                    @JsonProperty("sound") int sound,
                    @JsonProperty("brightness") int brightness,
                    @JsonProperty("batteryPerc") int batteryPerc,
                    @JsonProperty("batteryMinutes") int batteryMinutes,
                    @JsonProperty("batteryChargeRate") int batteryChargeRate,
                    @JsonProperty("batteryDischargeRate") int batteryDischargeRate,
                    @JsonProperty("cpuLevel") int cpuLevel,
                    @JsonProperty("gpuLevel") int gpuLevel,
                    @JsonProperty("gpuTemp") int gpuTemp,
                    @JsonProperty("ramLevel") int ramLevel,
                    @JsonProperty("storageLevel") int storageLevel,
                    @JsonProperty("airplane") boolean airplane,
                    @JsonProperty("mute") boolean mute,
                    @JsonProperty("redLight") boolean redLight,
                    @JsonProperty("saveBattery") boolean saveBattery,
                    @JsonProperty("hotspot") boolean hotspot,
                    @JsonProperty("isClipboardShared") boolean isClipboardShared,
                    @JsonProperty("redLightLevel") int redLightLevel) {
        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.batteryPlugged = batteryPlugged;
        this.sound = sound;
        this.brightness = brightness;
        this.batteryPerc = batteryPerc;
        this.batteryMinutes = batteryMinutes;
        this.cpuLevel = cpuLevel;
        this.ramLevel = ramLevel;
        this.gpuLevel=gpuLevel;
        this.gpuTemp=gpuTemp;
        this.airplane = airplane;
        this.mute = mute;
        this.redLight = redLight;
        this.saveBattery = saveBattery;
        this.hotspot = hotspot;
        this.redLightLevel = redLightLevel;
        this.storageLevel=storageLevel;
        this.isClipboardShared=isClipboardShared;
        this.batteryChargeRate=batteryChargeRate;
        this.batteryDischargeRate=batteryDischargeRate;
        updated = LocalDateTime.now(ZoneOffset.UTC);
    }

    public PcStatus(String wifi, String bluetooth, String batteryPlugged, String airplane,
                    String mute, String redLight, String saveBattery, String hotspot, String isClipboardShared,
                    String sound, String brightness, String batteryPerc, String batteryMinutes, String cpuLevel, String ramLevel,
                    String redLightLevel, String storageLevel, String gpuLevel, String gpuTemp, String batteryChargeRate,
                    String batteryDischargeRate) {
        this.wifi = Boolean.parseBoolean(wifi);
        this.bluetooth = Boolean.parseBoolean(bluetooth);
        this.batteryPlugged = Boolean.parseBoolean(batteryPlugged);
        this.airplane = Boolean.parseBoolean(airplane);
        this.mute = Boolean.parseBoolean(mute);
        this.redLight = Boolean.parseBoolean(redLight);
        this.saveBattery = Boolean.parseBoolean(saveBattery);
        this.hotspot = Boolean.parseBoolean(hotspot);
        this.isClipboardShared = Boolean.parseBoolean(isClipboardShared);
        this.sound = Integer.parseInt(sound);
        this.brightness = Integer.parseInt(brightness);
        this.batteryPerc = Integer.parseInt(batteryPerc);
        this.batteryMinutes = Integer.parseInt(batteryMinutes);
        this.cpuLevel = Integer.parseInt(cpuLevel);
        this.ramLevel = Integer.parseInt(ramLevel);
        this.redLightLevel = Integer.parseInt(redLightLevel);
        this.storageLevel = Integer.parseInt(storageLevel);
        this.gpuLevel = Integer.parseInt(gpuLevel);
        this.gpuTemp = Integer.parseInt(gpuTemp);
        this.batteryChargeRate = Integer.parseInt(batteryChargeRate);
        this.batteryDischargeRate = Integer.parseInt(batteryDischargeRate);
    }

    public boolean isClipboardShared() {
        return isClipboardShared;
    }

    public int getStorageLevel() {
        return storageLevel;
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

    public int getGpuLevel() {
        return gpuLevel;
    }

    public int getGpuTemp() {
        return gpuTemp;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public int getBatteryChargeRate() {
        return batteryChargeRate;
    }

    public int getBatteryDischargeRate() {
        return batteryDischargeRate;
    }

    public void setWattage(int wattage) {
        this.wattage = wattage;
    }

    public int getWattage(){
        return wattage;
    }
}
