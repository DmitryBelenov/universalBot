package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.MyWorldStates;
import bot.factory.handlers.interfaces.Command;
import bot.factory.handlers.interfaces.Response;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyWorldHideContactResponse implements Response {

    private Update update;

    MyWorldHideContactResponse(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getMessage();
        String input = message.getText();

        String response = "Well done!\uD83D\uDE03\nYou shared you world to Universal Bot users \uD83C\uDF04\nSee all pics: /my_world command";

        Integer userId = message.getFrom().getId();
        if (input.trim().equals("yes") || input.trim().equals("no")){
            boolean keepState = true;
            if (input.equals("yes")){
                DBUtils db = new DBUtils();
                int res = db.hideMyWorldContact(userId);
                db.connectionClose();

                if (res == -1){
                    response = "Hmm.. Looks like some problem\nTry again by /set_my_world command";
                    keepState = false;
                }
            }

            if (!keepState) {
                AliasMapManager.myWorldStatesMap.remove(userId);
            } else {
                AliasMapManager.myWorldStatesMap.put(userId, MyWorldStates.set_contact_hiding);
            }
        } else {
            response = "Incorrect input\nWrite me 'yes' or 'no'";
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), response);
    }

    @Override
    public String getAlias() {
        return null;
    }
}
