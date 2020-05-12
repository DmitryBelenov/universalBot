package bot.factory.handlers.impl.commands;

import bot.factory.handlers.interfaces.Command;
import org.telegram.telegrambots.meta.api.objects.Update;

public class YoLookAroundCommand implements Command {

    public static final String alias = "/yo";
    private Update update;

    YoLookAroundCommand(Update update) {
        this.update = update;
    }

    @Override
    public <T> T invoke() {
        return null;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
