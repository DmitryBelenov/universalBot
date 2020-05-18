package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedList;
import java.util.List;


public class VideoNotesCommand implements Command {

    public static final String alias = "/vnotes";
    private Update update;

    VideoNotesCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();

        List<SendVideoNote> vNotes = getVNotesList(chatId);
        if (vNotes.size() > 0) {
            List<Object> set = new LinkedList<>(vNotes);

            ResponseMessage rm = new ResponseMessage();
            SendMessage msg = rm.fillMessage(message, "Total videos: "+vNotes.size(), true);

            set.add(msg);

            return (T) set;
        } else {
            ResponseMessage rm = new ResponseMessage();
            return (T) rm.fillMessage(message, "No shared video yet:(\nBe first, share your video note by\n'SET V NOTE' button!", true);
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    private List<SendVideoNote> getVNotesList(Long chatId) {
        DBUtils db = new DBUtils();
        List<SendVideoNote> vNotes = db.getVideoNotes(chatId);
        db.connectionClose();
        return vNotes;
    }
}
