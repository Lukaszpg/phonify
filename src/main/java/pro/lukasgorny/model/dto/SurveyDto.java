package pro.lukasgorny.model.dto;

public class SurveyDto {
    private String screenSize;
    private String internalMemorySize;

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
}