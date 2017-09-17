package pro.lukasgorny.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.lukasgorny.model.dto.DeviceDto;
import pro.lukasgorny.util.Commons;
import pro.lukasgorny.util.JenaProperties;
import pro.lukasgorny.util.enums.InternalMemorySize;
import pro.lukasgorny.util.enums.ScreenSize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParserService {

    private HttpService httpService;
    private FileService fileService;

    private List<DeviceDto> devices;
    private String response;
    private Model model;

    @Autowired
    public ParserService(HttpService httpService, FileService fileService) {
        this.httpService = httpService;
        this.fileService = fileService;
    }

    public void parse() throws IOException {
        sendRequestToApi();
        parseResponseToList();
        prepareData();
        createJenaModel();
        populateModel();
        writeModelToFile();
    }

    private void sendRequestToApi() {
        try {
            response = httpService.sendGetRequest(Commons.FONOAPI_GET_LATEST_LINK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseResponseToList() {
        Gson gson = new Gson();
        devices = gson.fromJson(response, new TypeToken<List<DeviceDto>>() {
        }.getType());
    }

    private void prepareData() {
        devices = deleteYearFromDeviceName(devices);
        devices = setDevicesFields(devices);
    }

    private void createJenaModel() {
        model = ModelFactory.createDefaultModel();
        model.setNsPrefix("feature", Commons.RDF_NAMESPACE);
    }

    private void populateModel() {
        devices.forEach(deviceDto -> {
            if (deviceDto.internalMemorySizes != null && deviceDto.internalMemorySizes.size() > 1) {
                createMultipleDevices(deviceDto);
            } else {
                createSingleDevice(deviceDto);
            }
        });
    }

    private void createMultipleDevices(DeviceDto deviceDto) {
        deviceDto.internalMemorySizes.keySet().forEach(memorySize -> {
            Resource someDevice = model.createResource(Commons.RDF_NAMESPACE + deviceDto.deviceName.replaceAll(Commons.Regex.ALL_WHITESPACE, "")
                    + deviceDto.internalMemorySizes.get(memorySize));
            someDevice.addProperty(JenaProperties.internalMemorySize, memorySize);
            someDevice.addProperty(JenaProperties.screenSize, deviceDto.screenSizeString);

            if(deviceDto.internalMemoryUnit.equals("GB")) {
                someDevice.addProperty(JenaProperties.deviceName, deviceDto.deviceName + " " + deviceDto.internalMemorySizes.get(memorySize) + deviceDto.internalMemoryUnit);
            } else {
                someDevice.addProperty(JenaProperties.deviceName, deviceDto.deviceName + " " + deviceDto.internalMemorySizes.get(memorySize));
            }
        });
    }

    private void createSingleDevice(DeviceDto deviceDto) {
        Resource someDevice = model.createResource(Commons.RDF_NAMESPACE + deviceDto.deviceName.replaceAll(Commons.Regex.ALL_WHITESPACE, ""));

        if (deviceDto.internalMemorySizes != null) {
            String memoryToAdd = deviceDto.internalMemorySizes.keySet().stream().findFirst().get();
            someDevice.addProperty(JenaProperties.internalMemorySize, memoryToAdd);
        }

        someDevice.addProperty(JenaProperties.screenSize, deviceDto.screenSizeString);
        someDevice.addProperty(JenaProperties.deviceName, deviceDto.deviceName);
    }

    private void writeModelToFile() {
        try {
            fileService.writeFileToDisk(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<DeviceDto> deleteYearFromDeviceName(List<DeviceDto> devices) {
        devices.forEach(deviceDto -> deviceDto.deviceName = deviceDto.deviceName.replaceAll(Commons.Regex.YEAR_IN_DEVICE_NAME, ""));
        return devices;
    }

    private List<DeviceDto> setDevicesFields(List<DeviceDto> devices) {
        devices.forEach(deviceDto -> {
            deviceDto.screenSizeString = calculateScreenSize(deviceDto.screenSizeString);
            deviceDto = prepareInternalMemoryList(deviceDto);
        });
        return devices;
    }

    private String calculateScreenSize(String screenSizeString) {
        Double screenSizeDouble = Double.valueOf(screenSizeString.replaceAll(Commons.Regex.SCREEN_SIZE, ""));

        if (screenSizeDouble <= 3.0) {
            return ScreenSize.small.name();
        } else if (screenSizeDouble > 3.0 && screenSizeDouble < 5.0) {
            return ScreenSize.medium.name();
        } else {
            return ScreenSize.big.name();
        }
    }

    private DeviceDto prepareInternalMemoryList(DeviceDto deviceDto) {
        String internalMemorySizeString  = deviceDto.internalMemorySizeString;
        if (internalMemorySizeString != null) {
            internalMemorySizeString = internalMemorySizeString.replaceAll(Commons.Regex.INTERNAL_MEMORY_SIZE, "");

            boolean shouldCalculateAsGigabytes = shouldCalcateAsGigabytes(internalMemorySizeString);

            internalMemorySizeString = internalMemorySizeString.replaceAll(Commons.Regex.INTERNAL_MEMORY_SIZE_UNIT, "");

            String[] memorySizes = internalMemorySizeString.split("/");
            Map<String, String> resultMap = new HashMap<>();

            for (String memorySize : memorySizes) {
                if (shouldCalculateAsGigabytes) {
                    resultMap.put(calculateInternalMemorySizeAsGigabytes(Double.valueOf(memorySize)), memorySize);
                    deviceDto.internalMemoryUnit = "GB";
                } else {
                    resultMap.put(calculateInternalMemorySizeAsMegabytes(Double.valueOf(memorySize)), memorySize);
                }
            }

            deviceDto.internalMemorySizes = resultMap;
        }

        return deviceDto;
    }

    private boolean shouldCalcateAsGigabytes(String memoryString) {
        return memoryString.contains("GB");
    }

    private String calculateInternalMemorySizeAsGigabytes(Double memorySize) {
        if (memorySize <= 16.0) {
            return InternalMemorySize.small.name();
        } else if (memorySize > 16.0 && memorySize <= 32.0) {
            return InternalMemorySize.medium.name();
        } else {
            return InternalMemorySize.big.name();
        }
    }

    private String calculateInternalMemorySizeAsMegabytes(Double memorySize) {
        if (memorySize <= 1000.0) {
            return InternalMemorySize.small.name();
        } else if (memorySize > 1000.0 && memorySize <= 6000.0) {
            return InternalMemorySize.medium.name();
        } else {
            return InternalMemorySize.big.name();
        }
    }
}