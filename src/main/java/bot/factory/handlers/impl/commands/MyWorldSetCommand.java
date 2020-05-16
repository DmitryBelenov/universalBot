package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.MyWorldStates;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyWorldSetCommand implements Command {

    public static final String alias = "/set_my_world";
    private Update update;

    MyWorldSetCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getCallbackQuery().getMessage();

        String response = "Send me your world as one pic or selfie \uD83D\uDCF7";

        Integer from = update.getCallbackQuery().getFrom().getId();
        int available = isNewRecordAvailable(from);

        if (available == 1) {
            AliasMapManager.myWorldStatesMap.put(from, MyWorldStates.my_world_requested);
        } else if (available == 0){
            response = "Image count limit exceeded\nTry to share your world later";
        } else if (available == -1){
            response = "You already share your pic\uD83D\uDCF7\nSee it by 'MW' button\nYou can share a new photo in an hour";
        } else {
            response = "Hmm.. Looks like some problem\nTry again later please";
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(message, response, true);
    }

    @Override
    public String getAlias() {
        return null;
    }

    private int isNewRecordAvailable(Integer userId){
        DBUtils db = new DBUtils();
        int res = db.checkMyWorldAvailability(userId);
        db.connectionClose();
        return res;
    }
}
