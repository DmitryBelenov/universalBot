package bot.factory.handlers.impl.responses;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.MyWorldStates;
import bot.factory.handlers.interfaces.Response;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
        CallbackQuery cb = update.getCallbackQuery();
        Message message = cb.getMessage();
        String input = cb.getData();

        String response = "Well done!\uD83D\uDE03\nYou shared you world to Universal Bot users \uD83C\uDF04\nSee all pics by\n'MW' button";

        Integer userId = cb.getFrom().getId();

        boolean keepState = true;
        if (input.equals("yes")) {
            DBUtils db = new DBUtils();
            int res = db.hideMyWorldContact(userId);
            db.connectionClose();

            if (res == -1) {
                response = "Hmm.. Looks like some problem\nTry again by\n'SET MW' button";
                keepState = false;
            }
        }

        if (!keepState) {
            AliasMapManager.myWorldStatesMap.remove(userId);
        } else {
            AliasMapManager.myWorldStatesMap.put(userId, MyWorldStates.set_contact_hiding);
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(message, response, true);
    }
}
