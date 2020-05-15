package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.MyWorldStates;
import bot.factory.handlers.interfaces.Response;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyWorldDescriptionResponse implements Response {

    private Update update;

    MyWorldDescriptionResponse(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getMessage();
        String input = message.getText();

        String response = "OK, do you want keep your contact hidden?\n- yes\n- no";

        Integer userId = message.getFrom().getId();
        if (input.trim().equals("no") || (input.startsWith("yes ") && input.length() > 4)){
            boolean keepState = true;
            if (input.startsWith("yes ")){
                String description = input.substring(4);
                DBUtils db = new DBUtils();
                int res = db.addDescriptionToMyWorld(userId, description);
                db.connectionClose();

                if (res == -1){
                    response = "Hmm.. Looks like some problem\nTry again by /set_my_world command";
                    keepState = false;
                }
            }

            if (!keepState) {
                AliasMapManager.myWorldStatesMap.remove(userId);
            } else {
                AliasMapManager.myWorldStatesMap.put(userId, MyWorldStates.description_requested);
            }
        } else {
            response = "Incorrect input\nWrite me 'yes <pic description>' or 'no'";
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), response);
    }

    @Override
    public String getAlias() {
        return null;
    }
}
