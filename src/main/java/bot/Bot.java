package bot;

import bot.factory.BotFactory;
import bot.property.BotProperties;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private static Logger log = Logger.getLogger(Bot.class);
    private static boolean process = false;

    Bot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!process) {
            process = true;
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    BotFactory factory = new BotFactory(update);
                    sendResponse(factory.getResponse());
                    process = false;
                }
            });
            th.start();
        }
    }

    @Override
    public String getBotUsername() {
        return BotProperties.get().getProperty("bot.name");
    }

    @Override
    public String getBotToken() {
        return BotProperties.get().getProperty("bot.token");
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> void sendResponse(T t) {
        if (t instanceof List){
           List<T> list = (List<T>) t;
           for (T method : list){
               response(method);
           }
        } else {
            response(t);
        }
    }

    private <T> void response(T t){
        if (t instanceof SendMessage) {
            SendMessage s =(SendMessage) t;
            try {
                execute(s);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Message\n"+e);
            }
        } else if (t instanceof SendAnimation) {
            SendAnimation animation = (SendAnimation) t;
            try {
                execute(animation);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Animation\n"+e);
            }
        } else if (t instanceof SendAudio) {
            SendAudio audio = (SendAudio) t;
            try {
                execute(audio);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Audio\n"+e);
            }
        } else if (t instanceof SendChatAction) {

        } else if (t instanceof SendContact) {
            SendContact contact = (SendContact) t;
            try {
                execute(contact);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Contact\n"+e);
            }
        } else if (t instanceof SendDice) {

        } else if (t instanceof SendDocument) {

        } else if (t instanceof SendGame) {

        } else if (t instanceof SendInvoice) {

        } else if (t instanceof SendLocation) {

        } else if (t instanceof SendMediaGroup) {
            SendMediaGroup mediaGroup = (SendMediaGroup) t;
            try {
                execute(mediaGroup);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Media Group\n"+e);
            }
        }  else if (t instanceof SendPhoto) {
            SendPhoto photo = (SendPhoto) t;
            try {
                execute(photo);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Photo\n"+e);
            }
        } else if (t instanceof SendSticker) {

        } else if (t instanceof SendVenue) {

        } else if (t instanceof SendVideo) {
            SendVideo video = (SendVideo) t;
            try {
                execute(video);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Video\n"+e);
            }
        } else if (t instanceof SendVideoNote) {

        } else if (t instanceof SendVoice) {

        }
    }
}
