package bot.factory;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.YoStates;
import bot.factory.handlers.impl.commands.*;
import bot.factory.handlers.interfaces.Command;
import bot.factory.handlers.interfaces.CommandFactory;
import bot.factory.handlers.interfaces.Response;
import bot.factory.handlers.interfaces.ResponseFactory;
import com.google.common.base.Strings;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotFactory {

    private Update update;

    public BotFactory(Update update) {
        this.update = update;
    }

    public <T> T getResponse() {
        Message msg = update.getMessage();

        String text = msg.getText();
        if (!Strings.isNullOrEmpty(text)) {
            if (text.startsWith("/")) {
                CommandFactory commandFactory = getCommand(text);
                if (commandFactory != null) {
                    Command command = commandFactory.getFactory();
                    return command.invoke();
                }
            } else {
                ResponseFactory responseFactory = getResponse(text);
                if (responseFactory != null) {
                    Response response = responseFactory.getFactory();
                    return response.invoke();
                }
            }
        }

        Location location = msg.getLocation();
        if (location != null) {
           return handleLocation(msg.getFrom().getId());
        }

        Contact contact = msg.getContact();
        if (contact != null){
           return handleContact(msg.getFrom().getId(), contact);
        }

        return null;
    }

    private CommandFactory getCommand(String text) {
        if (InfoCommand.alias.equals(text)) {
            return new InfoCommandFactory(update);
        } else if (text.startsWith(SearchCommand.alias)) {
            return new SearchCommandFactory(update);
        } else if (text.startsWith(GoToCommand.alias)) {
            return new GoToCommandFactory(update);
        } else if (text.startsWith(YoCommand.alias)) {
            return new YoCommandFactory(update);
        } else if (text.startsWith(YoLookAroundCommand.alias)) {
            return new YoLookAroundCommandFactory(update);
        }
        return null;
    }

    private ResponseFactory getResponse(String text) {
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T>T handleLocation(Integer userId){
        YoStates yoState = AliasMapManager.yoStatesMap.get(userId);
        String goToKey = AliasMapManager.locationKeysMap.get(userId);

        if (yoState != null && yoState.equals(YoStates.set_contact_data) && goToKey == null){
            CommandFactory commandFactory = new YoLocationCommandFactory(update);
            Command command = commandFactory.getFactory();
            return command.invoke();
        }

        if (goToKey != null) {
            CommandFactory commandFactory = new GoToLocationsCommandFactory(update);
            Command command = commandFactory.getFactory();
            return command.invoke();
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), "Ok, I'll send pizza to this location:)");
    }

    @SuppressWarnings("unchecked")
    private <T>T handleContact(Integer userId, Contact contact){
        YoStates yoState = AliasMapManager.yoStatesMap.get(userId);
        if (yoState != null && yoState.equals(YoStates.set_interest)){
            CommandFactory commandFactory = new YoContactCommandFactory(update);
            Command command = commandFactory.getFactory();
            return command.invoke();
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), "You want me to call "+contact.getFirstName()+"?\nJoke:)");
    }
}
