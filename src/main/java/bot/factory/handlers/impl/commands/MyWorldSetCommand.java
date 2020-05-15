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

    public MyWorldSetCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getMessage();

        String response = "Send me your world as one pic or selfie \uD83D\uDCF7";

        int available = isNewRecordAvailable(message.getFrom().getId());

        if (available == 1) {
            AliasMapManager.myWorldStatesMap.put(message.getFrom().getId(), MyWorldStates.my_world_requested);
        } else if (available == 0){
            response = "Image count limit exceeded\nTry to share your world later";
        } else if (available == -1){
            response = "You already share your pic\uD83D\uDCF7\nSee it by /my_world command\nYou can share a new photo in an hour";
        } else {
            response = "Hmm.. Looks like some problem\nTry again later please";
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), response);
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
