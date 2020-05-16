package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements Command {

    public static final String alias = "/start";
    private Update update;

    StartCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getMessage();

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(message, "Hi!\uD83D\uDE03\nHere link to quick intro video\nhttps://yadi.sk/i/VbndJaGMCJz8ew\nUse buttons below to begin", true);
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
