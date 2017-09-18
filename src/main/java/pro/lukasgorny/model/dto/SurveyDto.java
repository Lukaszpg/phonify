package pro.lukasgorny.model.dto;

public class SurveyDto {
    private String screenSize;
    private String internalMemorySize;
    private String musicJack;

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
}