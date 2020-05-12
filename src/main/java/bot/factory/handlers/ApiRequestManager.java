package bot.factory.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class ApiRequestManager {

    private String request_url;

    public ApiRequestManager(String request_url) {
        this.request_url = request_url;
    }

    public String doGet(){
        HttpURLConnection connection = null;

        StringBuilder json = new StringBuilder();
        try {
            if (request_url != null) {
                URL url = new URL(request_url);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    String inputLine;


                    while ((inputLine = in.readLine()) != null) {
                        json.append(inputLine);
                    }
                    in.close();
                } else {
                    System.out.println("GET request '" + request_url + "' not worked");
                }
            }
        } catch (Exception e) {
            System.out.println("GET request error:\n"+e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return json.toString();
    }
}
