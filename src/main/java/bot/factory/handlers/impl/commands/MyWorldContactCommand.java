package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.MyWorldStates;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyWorldContactCommand implements Command {

    private Update update;

    MyWorldContactCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String response = "Nope) Send me YOUR contact\nStart over with the 'SET MV' button";

        Message message = update.getMessage();
        Contact contact = message.getContact();

        Integer userId = message.getFrom().getId();
        Integer contact_userId = contact.getUserID();

        boolean keepState = true;
        if (userId.equals(contact_userId)) {
            String name = contact.getFirstName();
            String phone = contact.getPhoneNumber();

            int appendContact = addContactToMyWorldInfo(userId, name, phone);

            if (appendContact == -1){
                response = "Hmm.. Looks like some problem\nTry again by /set_my_world command";
                keepState = false;
            } else {
                response = "OK, do you want attach description?\n- yes <your description>\n- no";
            }
        } else {
            keepState = false;
        }

        if (!keepState) {
            AliasMapManager.myWorldStatesMap.remove(userId);
        } else {
            AliasMapManager.myWorldStatesMap.put(userId, MyWorldStates.contact_sent);
        }
        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), response);
    }

    @Override
    public String getAlias() {
        return null;
    }

    private int addContactToMyWorldInfo(Integer userId, String name, String phone){
        DBUtils db = new DBUtils();
        int res = db.addContactToMyWorld(userId, name, phone);
        db.connectionClose();
        return res;
    }
}
