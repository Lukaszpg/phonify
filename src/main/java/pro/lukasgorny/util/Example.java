package pro.lukasgorny.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import pro.lukasgorny.model.dto.DeviceDto;
import pro.lukasgorny.service.http.HttpService;
import pro.lukasgorny.util.enums.ScreenSize;


public class Example {
    public void main(String[] args) throws IOException {
        HttpService httpService = new HttpService(Commons.FONOAPI_TOKEN);
        String response = httpService.sendGetRequest(Commons.FONOAPI_GET_LATEST_LINK);

        Gson gson = new Gson();
        List<DeviceDto> deviceDtos = gson.fromJson(response, new TypeToken<List<DeviceDto>>(){}.getType());

        deviceDtos = deleteYearFromDeviceName(deviceDtos);
        deviceDtos = setDevicesFields(deviceDtos);

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("", Commons.RDF_NAMESPACE);

        deviceDtos.forEach(deviceDto -> {
            Resource someDevice = model.createResource(Commons.RDF_NAMESPACE + deviceDto.deviceName.replaceAll(Commons.Regex.ALL_WHITESPACE,""));
            someDevice.addProperty(JenaProperties.screenSize, deviceDto.screenSizeString);
            someDevice.addProperty(JenaProperties.deviceName, deviceDto.deviceName);
        });

        writeRDFModelToFile(model);
        printDevicesWithBigScreen();
    }

    private List<DeviceDto> deleteYearFromDeviceName(List<DeviceDto> deviceDtos) {
        deviceDtos.forEach(deviceDto -> deviceDto.deviceName = deviceDto.deviceName.replaceAll(Commons.Regex.YEAR_IN_DEVICE_NAME, ""));
        return deviceDtos;
    }

    private List<DeviceDto> setDevicesFields(List<DeviceDto> deviceDtos) {
        deviceDtos.forEach(deviceDto -> {
            deviceDto.screenSizeString = calculateScreenSize(deviceDto.screenSizeString);
        });
        return deviceDtos;
    }

    private String calculateScreenSize(String screenSizeString) {
        Double screenSizeDouble = Double.valueOf(screenSizeString.replaceAll(Commons.Regex.SCREEN_SIZE, ""));

        if(screenSizeDouble < 3.0) {
            return ScreenSize.small.name();
        } else if(screenSizeDouble > 3.0 && screenSizeDouble < 5.0) {
            return ScreenSize.medium.name();
        } else {
            return ScreenSize.big.name();
        }
    }

    private void writeRDFModelToFile(Model model) throws IOException {
        FileWriter out = new FileWriter(Commons.RDF_FILENAME);
        try {
            model.write( out, Commons.RDF_TYPE);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
    }

    private void printDevicesWithBigScreen() {
        Model model = RDFDataMgr.loadModel(Commons.RDF_FILENAME, Lang.RDFXML);
        List<Resource> devices = model.listResourcesWithProperty(JenaProperties.screenSize, model.createLiteral(ScreenSize.big.name())).toList();

        devices.forEach(device -> System.out.println(device.getProperty(JenaProperties.deviceName).getLiteral()));
    }
}

