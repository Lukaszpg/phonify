package pro.lukasgorny.model.dto;

public class SurveyDto {
    private String screenSize;
    private String internalMemorySize;
    private String musicJack;
    private String dualCamera;
    private String primaryCameraQuality;
    private String hasDualSim;

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getInternalMemorySize() {
        return internalMemorySize;
    }

    public void setInternalMemorySize(String internalMemorySize) {
        this.internalMemorySize = internalMemorySize;
    }

    public String getMusicJack() {
        return musicJack;
    }

    public void setMusicJack(String musicJack) {
        this.musicJack = musicJack;
    }

    public String getDualCamera() {
        return dualCamera;
    }

    public void setDualCamera(String dualCamera) {
        this.dualCamera = dualCamera;
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
}