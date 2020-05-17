package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import bot.property.BotProperties;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedList;
import java.util.List;

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

        ResponseMessage rm1 = new ResponseMessage();
        SendMessage policy = rm1.fillMessage(message, "READ FIRST❗️\nCensorship policy of Universal Bot:" +
                "\n- NO INTIMATE&CRIMINAL PIC'S \uD83D\uDEAB" +
                "\n- NO SWEARING \uD83D\uDEAB");

        ResponseMessage rm2 = new ResponseMessage();
        SendMessage welcomeMsg = rm2.fillMessage(message, "Hi!\uD83D\uDE03\nHere link to quick intro video\n"+ BotProperties.get().getProperty("ubot.intro") +"\nUse buttons below to begin", true);

        List<Object> msgGroup = new LinkedList<>();
        msgGroup.add(policy);
        msgGroup.add(welcomeMsg);

        return (T) msgGroup;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
