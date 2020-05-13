package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.YoStates;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

public class YoCommand implements Command {

    public static final String alias = "/yo ";
    private Update update;
    private final List<String> listOfInterests = Arrays.asList("bar","walk","music","cinema","shopping","fun","travel");

    YoCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String response = "Cool! Now send me your contact";

        Message message = update.getMessage();
        String interests = message.getText().replace(YoCommand.alias, "").trim();

        StringBuilder sb = new StringBuilder();
        for (String interest : listOfInterests){
            sb.append(interest).append("\n");
        }
        String incorrect = "Send me you'r interests correctly\n\nExample:\n\\yo bar,fun\n\n*****************\nInterest list here:\n"+sb.toString();
        Integer userId = message.getFrom().getId();
        if (interests.trim().contains(",")){
            String[] iList = interests.split(",");

            boolean save = true;
            StringBuilder concat = new StringBuilder();
            int k = 0;
            for (String i : iList){
                if (!listOfInterests.contains(i.trim())){
                    response = incorrect;
                    save = false;
                    break;
                }
                concat.append(i).append(k == iList.length - 1 ? "" : ",");
                k++;
            }

            if (save){
                String yoList = concat.toString();
                response = setYoFirstData(userId, yoList, response);
            } else {
                response = incorrect;
            }
        } else {
            if (listOfInterests.contains(interests)){
                response = setYoFirstData(userId, interests, response);
            } else {
                response = incorrect;
            }
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), response);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    private String setYoFirstData(Integer userId, String yoList, String response){
        DBUtils db = new DBUtils();

        boolean changeState = true;
        int res = db.addUserYoFirstStage(userId, yoList);

        if (res == 1)
            response = "YO! I'm update your interests)\nSend me your contact";

        if (res == -1) {
            response = "Hmm.. Looks like some problem\nTry again)";
            changeState = false;
        }

        if (changeState) {
            AliasMapManager.yoStatesMap.putIfAbsent(userId, YoStates.set_interest);
        } else {
            AliasMapManager.yoStatesMap.remove(userId);
        }

        db.connectionClose();

        return response;
    }
}
