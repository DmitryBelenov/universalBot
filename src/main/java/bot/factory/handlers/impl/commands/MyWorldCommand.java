package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.util.LinkedList;
import java.util.List;

public class MyWorldCommand implements Command {

    public static final String alias = "/my_world";
    private Update update;

    MyWorldCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        List<InputMedia> worlds = getMyWorldList();

        Message message = update.getCallbackQuery().getMessage();
        if (worlds.size() > 0){
            Long chatId = message.getChatId();
            SendMediaGroup mediaGroup = new SendMediaGroup();
            mediaGroup.setChatId(chatId);
            mediaGroup.setMedia(worlds);

            ResponseMessage rm = new ResponseMessage();
            SendMessage sm = rm.fillMessage(message, "Total pictures: "+worlds.size(), true);

            List<Object> msgGroup = new LinkedList<>();
            msgGroup.add(mediaGroup);
            msgGroup.add(sm);

            return (T) msgGroup;
        } else {
            ResponseMessage rm = new ResponseMessage();
            return (T) rm.fillMessage(message, "No shared worlds yet:(\nBe first, share your pic or selfie by\n'SET MW' button!", true);
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    private List<InputMedia> getMyWorldList(){
        DBUtils db = new DBUtils();
        List<InputMedia> worlds = db.getMyWorlds();
        db.connectionClose();
        return worlds;
    }
}
