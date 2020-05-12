package bot.factory.handlers.impl.commands;

import bot.factory.handlers.interfaces.Command;
import bot.factory.handlers.interfaces.CommandFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class YoLocationCommandFactory implements CommandFactory {

    private Update update;

    public YoLocationCommandFactory(Update update) {
        this.update = update;
    }

    @Override
    public Command getFactory() {
        return new YoLocationCommand(update);
    }
}
