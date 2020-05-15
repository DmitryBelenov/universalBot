package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import org.telegram.telegrambots.meta.api.objects.Update;

public class InfoCommand implements Command {

    public static final String alias = "/info";
    private Update update;

    InfoCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String txt = "Universal Bot\uD83E\uDD16\n\nCreated by: Belenov Dmitry\ndmitrij_belenov@mail.ru\nhttps://t.me/booleanJ";

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), txt);
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
