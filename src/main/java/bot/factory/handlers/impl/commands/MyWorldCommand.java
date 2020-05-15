package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

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
        Message message = update.getMessage();

        List<InputMedia> worlds = getMyWorldList();

        if (worlds.size() > 0){
            Long chatId = message.getChatId();
            SendMediaGroup mediaGroup = new SendMediaGroup();
            mediaGroup.setChatId(chatId);
            mediaGroup.setMedia(worlds);

            return (T) mediaGroup;
        } else {
            ResponseMessage rm = new ResponseMessage();
            return (T) rm.fillMessage(update.getMessage(), "No shared worlds yet:(\nBe first, share your pic or selfie by /set_my_world command!");
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
