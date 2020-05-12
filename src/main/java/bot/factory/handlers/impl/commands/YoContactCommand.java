package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.YoStates;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class YoContactCommand implements Command {

    private Update update;

    YoContactCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String response = "Nope) Send me YOUR contact\nTry again by \\yo command";

        Message message = update.getMessage();
        Integer senderId = message.getFrom().getId();

        Contact contact = message.getContact();
        Integer contactId = contact.getUserID();

        boolean keepState = true;
        if (senderId.equals(contactId)){
            String name = contact.getFirstName();
            String phone = contact.getPhoneNumber();

            int res = setYoSecondData(senderId, name, phone);

            if (res == 1) {
                response = "Nice! Now send me your location";
            }

            if (res == -1){
                response = "Hmm.. Looks like some problem\nTry again by \\yo command";
                keepState = false;
            }
        } else {
            keepState = false;
        }

        if (!keepState) {
            AliasMapManager.yoStatesMap.remove(senderId);
        } else {
            AliasMapManager.yoStatesMap.put(senderId, YoStates.set_contact_data);
        }
        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), response);
    }

    @Override
    public String getAlias() {
        return null;
    }

    private int setYoSecondData(Integer userId, String name, String phone){
        DBUtils db = new DBUtils();
        int res = db.addUserYoSecondStage(userId, name, phone);
        db.connectionClose();
        return res;
    }
}
