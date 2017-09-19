package pro.lukasgorny.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import pro.lukasgorny.model.dto.DeviceDto;
import pro.lukasgorny.model.dto.ImportStatsDto;
import pro.lukasgorny.util.Commons;
import pro.lukasgorny.util.JenaProperties;
import pro.lukasgorny.util.enums.Brands;
import pro.lukasgorny.util.enums.CameraMatrix;
import pro.lukasgorny.util.enums.InternalMemorySize;
import pro.lukasgorny.util.enums.ScreenSize;

@Service
public class ParserService {

    private HttpService httpService;
    private FileService fileService;
    private long startTime;
    private long endTime;
    private Integer tabletCount;
    private Integer phoneCount;

    private List<DeviceDto> devices;
    private String response;
    private Model model;

    private ImportStatsDto importStatsDto;

    @Autowired
    public ParserService(HttpService httpService, FileService fileService) {
        this.httpService = httpService;
        this.fileService = fileService;
    }

    public ImportStatsDto parse() throws IOException {
        initialize();
        startTimer();
        createJenaModel();
        getDevicesFromMultipleBrands();
        writeModelToFile();
        stopTimer();
        prepareReturnDto();

        return importStatsDto;
    }

    private void initialize() {
        phoneCount = 0;
        tabletCount = 0;
    }

    private void getDevicesFromMultipleBrands() {
        for (Brands brand : Brands.values()) {
            sendRequestsToApi(brand.name());
            parseResponseToList();
            prepareData();
            populateModel();
        }
    }

    private void sendRequestsToApi(String brand) {
        try {
            response = httpService.sendGetRequest(brand);
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
            if(!isDeviceDiscontinued(deviceDto)) {
                if (deviceDto.internalMemorySizes != null && deviceDto.internalMemorySizes.size() > 1) {
                    createMultipleDevices(deviceDto);
                } else {
                    createSingleDevice(deviceDto);
                }

                countTabletOrPhone(deviceDto);
            }
        });
    }

    private void countTabletOrPhone(DeviceDto deviceDto) {
        if(deviceDto.isTablet.equals("yes")) {
            tabletCount++;
        } else {
            phoneCount++;
        }
    }

    private boolean isDeviceDiscontinued(DeviceDto deviceDto) {
        return StringUtils.containsIgnoreCase(deviceDto.status, "discontinued");
    }

    private void createMultipleDevices(DeviceDto deviceDto) {
        deviceDto.internalMemorySizes.keySet().forEach(memorySize -> {
            Resource someDevice = model.createResource(
                    Commons.RDF_NAMESPACE + deviceDto.deviceName.replaceAll(Commons.Regex.ALL_WHITESPACE, "") + deviceDto.internalMemorySizes
                            .get(memorySize));

            if(deviceDto.primaryCamera != null) {
                someDevice.addProperty(JenaProperties.hasDualCamera, deviceDto.hasDualCamera);
                someDevice.addProperty(JenaProperties.primaryCameraQuality, deviceDto.primaryCamera);
            }

            if(deviceDto.dualSim != null) {
                someDevice.addProperty(JenaProperties.hasDualSim, deviceDto.dualSim);
            }

            someDevice.addProperty(JenaProperties.musicJack, deviceDto.musicJack);
            someDevice.addProperty(JenaProperties.internalMemorySizeNumeric, deviceDto.internalMemorySizes.get(memorySize));
            someDevice.addProperty(JenaProperties.internalMemorySize, memorySize);
            someDevice.addProperty(JenaProperties.screenSizeNumeric, deviceDto.screenSizeNumeric);
            someDevice.addProperty(JenaProperties.screenSize, deviceDto.screenSizeString);
            someDevice.addProperty(JenaProperties.internalMemoryUnit, deviceDto.internalMemoryUnit);
            someDevice.addProperty(JenaProperties.isTablet, deviceDto.isTablet);

            if (deviceDto.internalMemoryUnit.equals("GB")) {
                someDevice.addProperty(JenaProperties.deviceName,
                        deviceDto.deviceName + " " + deviceDto.internalMemorySizes.get(memorySize) + deviceDto.internalMemoryUnit);
            } else {
                someDevice.addProperty(JenaProperties.deviceName, deviceDto.deviceName + " " + deviceDto.internalMemorySizes.get(memorySize));
            }
        });
    }

    private void createSingleDevice(DeviceDto deviceDto) {
        Resource someDevice = model.createResource(Commons.RDF_NAMESPACE + deviceDto.deviceName.replaceAll(Commons.Regex.ALL_WHITESPACE, ""));

        if (deviceDto.internalMemorySizes != null) {
            String memoryToAdd = deviceDto.internalMemorySizes.keySet().stream().findFirst().get();
            String memoryNumeric = deviceDto.internalMemorySizes.values().stream().findFirst().get();
            someDevice.addProperty(JenaProperties.internalMemorySizeNumeric, memoryNumeric);
            someDevice.addProperty(JenaProperties.internalMemorySize, memoryToAdd);
            someDevice.addProperty(JenaProperties.internalMemoryUnit, deviceDto.internalMemoryUnit);
        }

        if(deviceDto.primaryCamera != null) {
            someDevice.addProperty(JenaProperties.hasDualCamera, deviceDto.hasDualCamera);
            someDevice.addProperty(JenaProperties.primaryCameraQuality, deviceDto.primaryCamera);
        }

        if(deviceDto.dualSim != null) {
            someDevice.addProperty(JenaProperties.hasDualSim, deviceDto.dualSim);
        }

        someDevice.addProperty(JenaProperties.musicJack, deviceDto.musicJack);
        someDevice.addProperty(JenaProperties.screenSize, deviceDto.screenSizeString);
        someDevice.addProperty(JenaProperties.screenSizeNumeric, deviceDto.screenSizeNumeric);
        someDevice.addProperty(JenaProperties.deviceName, deviceDto.deviceName);
        someDevice.addProperty(JenaProperties.isTablet, deviceDto.isTablet);
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
            deviceDto.screenSizeNumeric = getScreenSizeNumeric(deviceDto.screenSizeString);
            deviceDto.screenSizeString = calculateScreenSize(deviceDto.screenSizeString);

            if(deviceDto.musicJack != null) {
                deviceDto.musicJack = doesDeviceHaveJack(deviceDto.musicJack);
            }

            if(deviceDto.primaryCamera != null) {
                deviceDto.hasDualCamera = doesDeviceHaveDualCamera(deviceDto.primaryCamera);
                deviceDto.primaryCamera = calculatePrimaryCameraMatrixSize(deviceDto.primaryCamera);
            }

            if(deviceDto.dualSim != null) {
                deviceDto.dualSim = doesDeviceHaveDualSim(deviceDto.dualSim);
            }

            deviceDto.isTablet = isDeviceATablet(deviceDto.deviceName);
            deviceDto = prepareInternalMemoryList(deviceDto);
        });
        return devices;
    }

    private String isDeviceATablet(String deviceName) {
        Pattern nameRegex = Pattern.compile(Commons.Regex.TABLET_NAME);
        Matcher matcher = nameRegex.matcher(deviceName);
        return matcher.find() ? "yes" : "no";
    }

    private String doesDeviceHaveDualSim(String sim) {
        return StringUtils.containsIgnoreCase(sim, "dual") ? "yes" : "no";
    }

    private String getScreenSizeNumeric(String screenSizeString) {
        return screenSizeString.replaceAll(Commons.Regex.SCREEN_SIZE, "");
    }

    private String calculateScreenSize(String screenSizeString) {
        String[] screenSizeSplit = screenSizeString.split(",");
        screenSizeString = screenSizeSplit[0];
        Double screenSizeDouble = Double.valueOf(screenSizeString.replaceAll(Commons.Regex.SCREEN_SIZE, ""));

        if (screenSizeDouble <= 3.0) {
            return ScreenSize.small.name();
        } else if (screenSizeDouble > 3.0 && screenSizeDouble <= 4.5) {
            return ScreenSize.medium.name();
        } else {
            return ScreenSize.big.name();
        }
    }

    private String doesDeviceHaveJack(String musicJack) {
        return StringUtils.containsIgnoreCase(musicJack, "yes") ? "yes" : "no";
    }

    private String doesDeviceHaveDualCamera(String primaryCamera) {
        return StringUtils.containsIgnoreCase(primaryCamera, "dual") ? "yes" : "no";
    }

    private DeviceDto prepareInternalMemoryList(DeviceDto deviceDto) {
        String internalMemorySizeString = deviceDto.internalMemorySizeString;
        if (internalMemorySizeString != null) {
            String[] memorySplitted = internalMemorySizeString.split(",");
            internalMemorySizeString = memorySplitted[0];
            boolean shouldCalculateAsGigabytes = shouldCalculateAsGigabytes(internalMemorySizeString);
            internalMemorySizeString = internalMemorySizeString.replaceAll(Commons.Regex.INTERNAL_MEMORY_SIZE_UNIT, "");

            String[] memorySizes = internalMemorySizeString.split("/");
            Map<String, String> resultMap = new HashMap<>();

            for (String memorySize : memorySizes) {
                if (shouldCalculateAsGigabytes) {
                    resultMap.put(calculateInternalMemorySizeAsGigabytes(Double.valueOf(memorySize)), memorySize);
                    deviceDto.internalMemoryUnit = "GB";
                } else {
                    resultMap.put(calculateInternalMemorySizeAsMegabytes(Double.valueOf(memorySize)), memorySize);
                    deviceDto.internalMemoryUnit = "MB";
                }
            }

            deviceDto.internalMemorySizes = resultMap;
        }

        return deviceDto;
    }

    private boolean shouldCalculateAsGigabytes(String memoryString) {
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

    private String calculatePrimaryCameraMatrixSize(String primaryCamera) {
        String[] primaryCameraSplit = primaryCamera.split(",");

        primaryCamera = primaryCameraSplit[0].replaceAll(Commons.Regex.PRIMARY_CAMERA_DUAL, "");
        primaryCamera = primaryCamera.replaceAll(Commons.Regex.PRIMARY_CAMERA_MP, "");

        if(StringUtils.containsIgnoreCase(primaryCamera, "vga")) {
            return CameraMatrix.average.name();
        }

        Double matrixSize = Double.valueOf(primaryCamera);

        if(matrixSize <= 4.0) {
            return CameraMatrix.average.name();
        } else if(matrixSize > 4.0 && matrixSize <= 8.0) {
            return CameraMatrix.good.name();
        } else {
            return CameraMatrix.very_good.name();
        }
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
    }

    private void stopTimer() {
        endTime = System.currentTimeMillis();
    }

    private void prepareReturnDto() {
        importStatsDto = new ImportStatsDto();
        importStatsDto.setImportTime(calculateExecutionTime());
        importStatsDto.setCount(phoneCount + tabletCount);
        importStatsDto.setCountPhone(phoneCount);
        importStatsDto.setCountTablet(tabletCount);
    }

    private Double calculateExecutionTime() {
        return (endTime - startTime) / 1000.0;
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