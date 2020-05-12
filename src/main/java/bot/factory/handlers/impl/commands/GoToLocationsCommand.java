package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ApiRequestManager;
import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.interfaces.Command;
import bot.property.BotProperties;
import com.google.common.base.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class GoToLocationsCommand implements Command {

    private static final String URL_PATTERN = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=1000&keyword=%s&key=%s";
    private Update update;

    GoToLocationsCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String result;

        Message msg = update.getMessage();

        Integer userId = msg.getFrom().getId();
        String alias = AliasMapManager.locationKeysMap.get(userId);

        Location location = msg.getLocation();
        Float latitude = location.getLatitude();
        Float longitude = location.getLongitude();

        final String GOOGLE_API_KEY = BotProperties.get().getProperty("google.api.key");

        String url = String.format(URL_PATTERN, String.valueOf(latitude), String.valueOf(longitude), alias, GOOGLE_API_KEY);
        String response = searchResult(url);
        if (!Strings.isNullOrEmpty(response)) {
            result = response;
        } else {
            result = "No results around you by keyword=" + alias;
        }

        AliasMapManager.locationKeysMap.remove(userId);


        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), result);
    }

    @Override
    public String getAlias() {
        return null;
    }

    private String searchResult(String reqUrl) {
        StringBuilder response = new StringBuilder();

        ApiRequestManager api = new ApiRequestManager(reqUrl);
        String json = api.doGet();

        if (!Strings.isNullOrEmpty(json)) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(json);
                JSONArray array = (JSONArray) object.get("results");

                int i = 0;
                for (Object o : array) {
                    JSONObject item = (JSONObject) o;
                    String name = (String) item.get("name");
                    String business_status = (String) item.get("business_status");
                    String vicinity = (String) item.get("vicinity");

                    JSONObject geometry = (JSONObject) item.get("geometry");
                    JSONObject location = (JSONObject) geometry.get("location");
                    Double lat = (Double) location.get("lat");
                    Double lng = (Double) location.get("lng");

                    String urlGooglePlacePattern = "https://www.google.com/maps/place/%s%s";

                    String latitude = String.valueOf(lat);
                    String longitude = String.valueOf(lng);
                    String mapLink = String.format(urlGooglePlacePattern,
                            (latitude.contains("-") ? latitude : "+" + latitude),
                            (longitude.contains("-") ? longitude : "+" + longitude));

                    response.append(name.toUpperCase())
                            .append("\n")
                            .append(vicinity)
                            .append("\n")
                            .append("status: ")
                            .append(business_status)
                            .append("\n")
                            .append(mapLink)
                            .append("\n\n");
                    i++;
                }

                if (i > 0) {
                    response.append("Total results: ").append(i);
                }

            } catch (ParseException e) {
                System.out.println("Json parse error:\n" + e);
            }
        }

        return response.toString();
    }
}
