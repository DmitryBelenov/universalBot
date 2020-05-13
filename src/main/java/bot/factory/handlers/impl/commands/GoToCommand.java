package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.interfaces.Command;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class GoToCommand implements Command {

    public static final String alias = "/goto ";
    private Update update;

    GoToCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getMessage();
        String target = message.getText().replace(GoToCommand.alias, "").trim();

        if (target.contains(" ")){
            String[] arr = target.split(" ");
            StringBuilder concat = new StringBuilder();
            int i = 0;
            for (String s : arr) {
                concat.append(s.trim())
                            .append(i == arr.length - 1 ? "" : ",");
                i++;
            }
            target = concat.toString();
        }

        Integer userId = message.getFrom().getId();

        AliasMapManager.locationKeysMap.put(userId, target);

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), "OK, now send me your Location \uD83D\uDCCD");
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
