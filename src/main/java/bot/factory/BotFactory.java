package bot.factory;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.MyWorldStates;
import bot.factory.handlers.impl.YoStates;
import bot.factory.handlers.impl.commands.*;
import bot.factory.handlers.impl.responses.MyWorldDescriptionResponseFactory;
import bot.factory.handlers.impl.responses.MyWorldHideContactResponseFactory;
import bot.factory.handlers.interfaces.Command;
import bot.factory.handlers.interfaces.CommandFactory;
import bot.factory.handlers.interfaces.Response;
import bot.factory.handlers.interfaces.ResponseFactory;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Arrays;
import java.util.List;

public class BotFactory {

    // 😊 - smile 😃 - laugh 🍕 - pizza 📍 - location 📱 - iphone 📷 - camera 🌄 - mountains pic 👤 - contact 🤖 - bot 🚫 - block it ❗️ - warning

    private static Logger log = Logger.getLogger(BotFactory.class);

    private Update update;

    public BotFactory(Update update) {
        this.update = update;
    }

    public <T> T getResponse() {
        if (update.hasMessage()) {
            Message msg = update.getMessage();
            String text = msg.getText();

            if (!Strings.isNullOrEmpty(text)) {
                if (text.startsWith("/") && !isStaticButton(text)) {
                    CommandFactory commandFactory = getCommand(text);
                    if (commandFactory != null) {
                        Command command = commandFactory.getFactory();
                        return command.invoke();
                    }
                } else {
                    ResponseFactory responseFactory = getResponse(msg);
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
            if (contact != null) {
                return handleContact(msg.getFrom().getId(), contact);
            }

            List<PhotoSize> photos = msg.getPhoto();
            if (photos != null) {
                return handlePicture(msg.getFrom().getId());
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery cb = update.getCallbackQuery();
            String data = cb.getData();
            if (Arrays.asList("yes", "no").contains(cb.getData())) {
                ResponseFactory responseFactory = getResponse(cb);
                if (responseFactory != null) {
                    Response response = responseFactory.getFactory();
                    return response.invoke();
                }
            } else {
                CommandFactory commandFactory = getCommand(data);
                if (commandFactory != null) {
                    Command command = commandFactory.getFactory();
                    return command.invoke();
                }
            }
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
        } else if (text.equals(YoLookAroundCommand.alias)) {
            return new YoLookAroundCommandFactory(update);
        } else if (text.equals(MyWorldCommand.alias)) {
            return new MyWorldCommandFactory(update);
        } else if (text.equals(MyWorldSetCommand.alias)) {
            return new MyWorldSetCommandFactory(update);
        } else if (text.equals(StartCommand.alias)) {
            return new StartCommandFactory(update);
        } else if (text.equals(ButtonsCommand.alias)) {
            return new ButtonsCommandFactory(update);
        }
        return null;
    }

    private ResponseFactory getResponse(CallbackQuery callbackQuery) {
        Integer userId = callbackQuery.getFrom().getId();

        MyWorldStates state = AliasMapManager.myWorldStatesMap.get(userId);
        if (state != null && state.equals(MyWorldStates.description_requested)) {
            return new MyWorldHideContactResponseFactory(update);
        }

        return null;
    }

    private ResponseFactory getResponse(Message message) {
        Integer userId = message.getFrom().getId();

        MyWorldStates state = AliasMapManager.myWorldStatesMap.get(userId);
        if (state != null && state.equals(MyWorldStates.contact_sent)) {
            return new MyWorldDescriptionResponseFactory(update);
        } else

            log.info("text/smile - [id" + userId + ":" + message.getFrom().getUserName() + ", " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + "]::[" + message.getText() + "]");
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T handleLocation(Integer userId) {
        YoStates yoState = AliasMapManager.yoStatesMap.get(userId);
        String goToKey = AliasMapManager.locationKeysMap.get(userId);

        if (yoState != null && yoState.equals(YoStates.set_contact_data) && goToKey == null) {
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
        return (T) rm.fillMessage(update.getMessage(), "Ok, I'll send pizza to this location:)\uD83C\uDF55", true);
    }

    @SuppressWarnings("unchecked")
    private <T> T handleContact(Integer userId, Contact contact) {
        YoStates yoState = AliasMapManager.yoStatesMap.get(userId);
        if (yoState != null && yoState.equals(YoStates.set_interest)) {
            CommandFactory commandFactory = new YoContactCommandFactory(update);
            Command command = commandFactory.getFactory();

            return command.invoke();
        }

        MyWorldStates state = AliasMapManager.myWorldStatesMap.get(userId);
        if (state != null && state.equals(MyWorldStates.picture_sent)) {
            CommandFactory commandFactory = new MyWorldContactCommandFactory(update);
            Command command = commandFactory.getFactory();
            return command.invoke();
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), "You want me to call " + contact.getFirstName() + "?\uD83D\uDE03", true);
    }

    @SuppressWarnings("unchecked")
    private <T> T handlePicture(Integer userId) {
        MyWorldStates state = AliasMapManager.myWorldStatesMap.get(userId);
        if (state != null && state.equals(MyWorldStates.my_world_requested)) {
            CommandFactory commandFactory = new MyWorldPictureCommandFactory(update);
            Command command = commandFactory.getFactory();
            return command.invoke();
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), "Nice pic \uD83C\uDF04", true);
    }

    private boolean isStaticButton(String command) {
        List<String> buttons = Arrays.asList(YoLookAroundCommand.alias,
                InfoCommand.alias,
                MyWorldCommand.alias,
                MyWorldSetCommand.alias);
        return buttons.contains(command);
    }
}
