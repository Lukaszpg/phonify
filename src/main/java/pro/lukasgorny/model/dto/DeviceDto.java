package pro.lukasgorny.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lukaszgo on 2017-09-12.
 */
public class DeviceDto {

    @SerializedName("DeviceName")
    public String deviceName;

    @SerializedName("size")
    public String screenSizeString;

    @SerializedName("internal")
    public String internalMemorySizeString;

    public List<String> internalMemorySizes;
}
