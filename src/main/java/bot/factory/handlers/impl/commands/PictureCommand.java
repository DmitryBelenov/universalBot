package bot.factory.handlers.impl.commands;

import bot.factory.handlers.interfaces.Command;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;

public class PictureCommand implements Command {

    public static String alias = "";
    private Update update;

    PictureCommand(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message msg = update.getMessage();
        SendPhoto photo = new SendPhoto();
        photo.setChatId(msg.getChatId());

        File initialFile = new File("D:\\1.jpg");
        InputStream targetStream = null;
        try {
            targetStream = new DataInputStream(new FileInputStream(initialFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (targetStream != null)
            photo.setPhoto(initialFile.getName(), targetStream);

        return (T) photo;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
