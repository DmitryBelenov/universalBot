package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ButtonsCommand implements Command {

    public static final String alias = "/buttons";
    private Update update;

    ButtonsCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), "Menu buttons:", true);
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
