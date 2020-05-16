package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ApiRequestManager;
import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import bot.property.BotProperties;
import com.google.common.base.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SearchCommand implements Command {

    public static final String alias = "/s ";
    private static final String URL_PATTERN = "https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&q=%s";
    private Update update;

    SearchCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String result;

        Message message = update.getMessage();
        String input = message.getText();
        String[] query = input.split(" ");

        if (query.length == 2) {
            String request = query[1];
            String response = searchResult(request);
            if (!Strings.isNullOrEmpty(response)) {
                result = response;
            } else {
                result = "No results, try another word";
            }
        } else {
            StringBuilder concat = new StringBuilder();
            int i = 0;
            for (String s : query) {
                if (i > 0) {
                    concat.append(s)
                            .append(i == query.length - 1 ? "" : "_");
                }
                i++;
            }

            String response = searchResult(concat.toString());
            if (!Strings.isNullOrEmpty(response)) {
                result = response;
            } else {
                result = "No results, try another word";
            }
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), result, true);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    private String searchResult(String query) {
        StringBuilder response = new StringBuilder();

        final String GOOGLE_API_KEY = BotProperties.get().getProperty("google.api.key");
        final String GOOGLE_CX = BotProperties.get().getProperty("google.cx.engine");

        String req = String.format(URL_PATTERN, GOOGLE_API_KEY, GOOGLE_CX, query);

        ApiRequestManager api = new ApiRequestManager(req);
        String json = api.doGet();

        if (!Strings.isNullOrEmpty(json)) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(json);

                JSONArray array = (JSONArray) object.get("items");

                // todo иногда нет никакого ответа из за npe здесь
                int i = 0;
                for (Object o : array) {
                    if (i <= 3) {
                        JSONObject item = (JSONObject) o;
                        String title = (String) item.get("title");
                        String link = (String) item.get("link");

                        response.append(title).append("\n").append(link).append("\n\n");
                        i++;
                    } else break;
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
