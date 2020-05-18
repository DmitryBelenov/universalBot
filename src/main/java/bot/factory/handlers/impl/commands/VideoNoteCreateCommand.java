package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.VideoNote;

public class VideoNoteCreateCommand implements Command {

    private Update update;

    VideoNoteCreateCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message msg = update.getMessage();

        VideoNote vNote = msg.getVideoNote();
        String fileId = vNote.getFileId();

        Integer userId = msg.getFrom().getId();

        DBUtils db = new DBUtils();
        int result = db.createVideoNote(userId, fileId);
        db.connectionClose();

        String response = "Well done!\uD83D\uDC4D\nYou shared your video note to Universal Bot users!\nSee all videos by\n'V NOTES' button";
        if (result == 1){
            AliasMapManager.videoNoteStatesMap.remove(userId);
        }

        if (result == -1){
            response = "Hmm.. Looks like some problem\nTry again by 'SET V NOTE' button";
            AliasMapManager.myWorldStatesMap.remove(userId);
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), response, true);
    }

    @Override
    public String getAlias() {
        return null;
    }
}
