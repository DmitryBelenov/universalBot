package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.YoStates;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class YoLocationCommand implements Command {

    private Update update;

    YoLocationCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        String response;

        Message message = update.getMessage();
        Integer senderId = message.getFrom().getId();

        Location location = message.getLocation();
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());

        int res = setYoThirdData(senderId, lat, lon);

        ResponseMessage rm = new ResponseMessage();
        if (res > 0) {
            AliasMapManager.yoStatesMap.put(senderId, YoStates.set_location);
            response = nearbyCheck(senderId, lat, lon);
            return (T) rm.fillMessage(update.getMessage(), response);
        } else {
            response = "Hmm.. Looks like some problem\nTry again by /yo command";
            AliasMapManager.yoStatesMap.remove(senderId);
            return (T) rm.fillMessage(update.getMessage(), response);
        }
    }

    @Override
    public String getAlias() {
        return null;
    }

    private int setYoThirdData(Integer userId, String lat, String lon) {
        DBUtils db = new DBUtils();
        int res = db.addUserYoThirdStage(userId, lat, lon);
        db.connectionClose();
        return res;
    }

    private String nearbyCheck(Integer senderId, String lat, String lon) {
        DBUtils db = new DBUtils();
        String res = db.nearbyYoCheck(senderId, lat, lon);
        db.connectionClose();
        return res;
    }
}
