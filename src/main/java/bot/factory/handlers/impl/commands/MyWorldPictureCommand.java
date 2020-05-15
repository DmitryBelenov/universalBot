package bot.factory.handlers.impl.commands;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.MyWorldStates;
import bot.factory.handlers.interfaces.Command;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class MyWorldPictureCommand implements Command {

    private Update update;

    MyWorldPictureCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message msg = update.getMessage();
        List<PhotoSize> list = msg.getPhoto();

        String response = "Your world is nice!\nNow send me your contact \uD83D\uDC64\nInput Line->Attach->Contact->Your Contact\nLater you can hide it";

        PhotoSize photo = list.get(0);
        String fileId = photo.getFileId();

        int result = createMyWorldRecord(msg.getFrom().getId(), fileId);

        Integer userId = msg.getFrom().getId();
        if (result == 1){
            AliasMapManager.myWorldStatesMap.put(userId, MyWorldStates.picture_sent);
        }

        if (result == -1){
            response = "Hmm.. Looks like some problem\nTry again by /set_my_world command";
            AliasMapManager.myWorldStatesMap.remove(userId);
        }

        ResponseMessage rm = new ResponseMessage();
        return (T) rm.fillMessage(update.getMessage(), response);
    }

    @Override
    public String getAlias() {
        return null;
    }

    private int createMyWorldRecord(Integer userId, String picId){
        DBUtils db = new DBUtils();
        int res = db.createMyWorld(userId, picId);
        db.connectionClose();
        return res;
    }


//        File initialFile = new File("D:\\1.jpg");
//        InputStream targetStream = null;
//        try {
//            targetStream = new DataInputStream(new FileInputStream(initialFile));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        if (targetStream != null)
//            photo.setPhoto(initialFile.getName(), targetStream);
}
