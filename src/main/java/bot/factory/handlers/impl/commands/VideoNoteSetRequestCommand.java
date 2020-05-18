package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.VNoteStates;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class VideoNoteSetRequestCommand implements Command {

    public static final String alias = "/set_vnote";
    private Update update;

    VideoNoteSetRequestCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getCallbackQuery().getMessage();
        Integer userId = update.getCallbackQuery().getFrom().getId();

        String response = "Send me your video note \uD83C\uDFA5";

        DBUtils db = new DBUtils();
        int res = db.checkVideoNotesAvailability(userId);
        db.connectionClose();

        if (res == 1) {
            AliasMapManager.videoNoteStatesMap.put(userId, VNoteStates.vnote_set_requested);
        } else if (res == 0) {
            response = "Video notes limit exceeded\nTry to share your note later";
        } else if (res == -1) {
            response = "You already share your video note \uD83C\uDFA5\nSee it by 'V NOTES' button\nYou can share a new video in an hour";
        } else {
            response = "Hmm.. Looks like some problem\nTry again later please";
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(message, response, true);
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
