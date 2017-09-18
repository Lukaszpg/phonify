package pro.lukasgorny.model.dto;

/**
 * Created by lukaszgo on 2017-09-18.
 */
public class ResultDeviceDto {
    private String screenSize;
    private String deviceName;
    private String memorySize;
    private String memoryUnit;
    private String musicJack;

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    public String getMemoryUnit() {
        return memoryUnit;
    }

    public void setMemoryUnit(String memoryUnit) {
        this.memoryUnit = memoryUnit;
    }

    public String getMusicJack() {
        return musicJack;
    }

    public void setMusicJack(String musicJack) {
        this.musicJack = musicJack;
    }
}
