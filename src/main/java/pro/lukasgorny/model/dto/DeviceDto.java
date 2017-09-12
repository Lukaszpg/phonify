package pro.lukasgorny.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lukaszgo on 2017-09-12.
 */
public class DeviceDto {

    @SerializedName("DeviceName")
    public String deviceName;

    @SerializedName("size")
    public String screenSizeString;
}
