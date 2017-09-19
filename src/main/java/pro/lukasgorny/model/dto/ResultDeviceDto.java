package pro.lukasgorny.model.dto;

public class ResultDeviceDto {
    private String screenSize;
    private String deviceName;
    private String memorySize;
    private String memoryUnit;
    private String musicJack;
    private String hasDualCamera;
    private String primaryCameraQuality;
    private String hasDualSim;
    private String isTablet;

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

    public String getHasDualCamera() {
        return hasDualCamera;
    }

    public void setHasDualCamera(String hasDualCamera) {
        this.hasDualCamera = hasDualCamera;
    }

    public String getPrimaryCameraQuality() {
        return primaryCameraQuality;
    }

    public void setPrimaryCameraQuality(String primaryCameraQuality) {
        this.primaryCameraQuality = primaryCameraQuality;
    }

    public String getHasDualSim() {
        return hasDualSim;
    }

    public void setHasDualSim(String hasDualSim) {
        this.hasDualSim = hasDualSim;
    }

    public String getIsTablet() {
        return isTablet;
    }

    public void setIsTablet(String isTablet) {
        this.isTablet = isTablet;
    }
}
