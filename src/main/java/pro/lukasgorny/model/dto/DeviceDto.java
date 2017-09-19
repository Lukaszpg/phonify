package pro.lukasgorny.model.dto;

import java.util.Map;
import com.google.gson.annotations.SerializedName;

public class DeviceDto {

    @SerializedName("DeviceName")
    public String deviceName;

    @SerializedName("size")
    public String screenSizeString;

    @SerializedName("internal")
    public String internalMemorySizeString;

    @SerializedName("_3_5mm_jack_")
    public String musicJack;

    @SerializedName("primary_")
    public String primaryCamera;

    @SerializedName("status")
    public String status;

    @SerializedName("sim")
    public String dualSim;

    public String hasDualCamera;

    public Map<String, String> internalMemorySizes;

    public String internalMemoryUnit;

    public String screenSizeNumeric;

    public String isTablet;
}
