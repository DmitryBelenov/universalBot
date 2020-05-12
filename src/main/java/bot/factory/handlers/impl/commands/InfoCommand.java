package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Date;

public class InfoCommand implements Command {

    public static final String alias = "/info";
    private Update update;

    InfoCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String txt = "Universal Bot\n\nCreated by: Belenov Dmitry\n\n"+new Date();

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), txt);
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
