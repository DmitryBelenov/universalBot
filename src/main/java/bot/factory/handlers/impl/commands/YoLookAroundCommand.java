package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class YoLookAroundCommand implements Command {

    public static final String alias = "/yo";
    private Update update;

    YoLookAroundCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String response;
        Message message = update.getCallbackQuery().getMessage();
        Integer senderId = update.getCallbackQuery().getFrom().getId();

        response = nearbyCheck(senderId);

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(message, response, true);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    private String nearbyCheck(Integer senderId) {
        DBUtils db = new DBUtils();
        String res = db.nearbyCheckByYoCommand(senderId);
        db.connectionClose();
        return res;
    }
}
