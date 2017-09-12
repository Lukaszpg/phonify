package pro.lukasgorny.service.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.springframework.stereotype.Component;

@Component
public class HttpService {

    private final String REQUEST_TYPE = "GET";
    private final String REQUEST_HEADER_USER_AGENT = "User-Agent";
    private final String REQUEST_HEADER_TOKEN = "?token=";
    private final String USER_AGENT = "Mozilla/5.0";


    private URL url;
    private String apiKey;
    private HttpURLConnection connection;

    public HttpService(final String apiKey) {
        this.apiKey = apiKey;
    }

    public String sendGetRequest(final String urlString) throws IOException {
        prepareURL(urlString);
        prepareAndOpenConnection();
        setRequestProperties();

        return processResponse();
    }

    private void prepareURL(String urlString) throws MalformedURLException {
        urlString += REQUEST_HEADER_TOKEN + apiKey;
        url = new URL(urlString);
    }

    private void prepareAndOpenConnection() throws IOException {
        connection = (HttpURLConnection) url.openConnection();
    }

    private void setRequestProperties() throws ProtocolException {
        connection.setRequestMethod(REQUEST_TYPE);
        connection.setRequestProperty(REQUEST_HEADER_USER_AGENT, USER_AGENT);
    }

    private String processResponse() throws IOException {
        int responseCode = connection.getResponseCode();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String output;
        StringBuilder response = new StringBuilder();

        while ((output = bufferedReader.readLine()) != null) {
            response.append(output);
        }

        bufferedReader.close();

        return response.toString();
    }
}