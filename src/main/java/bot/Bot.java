package bot;

import bot.factory.BotFactory;
import bot.property.BotProperties;
import bot.utils.DBUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private static Logger log = Logger.getLogger(Bot.class);

    Bot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @Override
    public void onUpdateReceived(Update update) {
            Thread th = new Thread(() -> {
                BotFactory factory = new BotFactory(update);
                Object response = factory.getResponse();
                if (response != null)
                    sendResponse(response);
            });
            th.start();
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
               response(method, true);
           }
        } else {
            response(t, false);
        }
    }

    private synchronized <T> void response(T t, boolean group){
        if (t instanceof SendMessage) {
            SendMessage s =(SendMessage) t;
            String chatId = ((SendMessage) t).getChatId();
            if (!group) {
                try {
                    cleanStack(chatId);
                } catch (Exception e) {
                    log.error("Unable to clean chat stack\n" + e);
                }
            }
            try {
                Message message = execute(s);
                storeMsg(chatId, message.getMessageId());
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Message\n"+e);
            }
        } else if (t instanceof SendAnimation) {
            SendAnimation animation = (SendAnimation) t;
            try {
                Message message = execute(animation);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Animation\n"+e);
            }
        } else if (t instanceof SendAudio) {
            SendAudio audio = (SendAudio) t;
            try {
                Message message = execute(audio);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Audio\n"+e);
            }
        } else if (t instanceof SendChatAction) {

        } else if (t instanceof SendContact) {
            SendContact contact = (SendContact) t;
            try {
                Message message = execute(contact);
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
            String chatId = mediaGroup.getChatId();
            try {
                cleanStack(chatId);
            } catch (Exception e){
                log.error("Unable to clean chat stack\n"+e);
            }
            try {
                List<Message> messages = execute(mediaGroup);
                for (Message m : messages){
                    storeMsg(chatId, m.getMessageId());
                }
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Media Group\n"+e);
            }
        }  else if (t instanceof SendPhoto) {
            SendPhoto photo = (SendPhoto) t;
            try {
                Message message = execute(photo);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Photo\n"+e);
            }
        } else if (t instanceof SendSticker) {

        } else if (t instanceof SendVenue) {

        } else if (t instanceof SendVideo) {
            SendVideo video = (SendVideo) t;
            try {
                Message message = execute(video);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Video\n"+e);
            }
        } else if (t instanceof SendVideoNote) {
            String chatId = ((SendVideoNote) t).getChatId();
            if (!group) {
                try {
                    cleanStack(chatId);
                } catch (Exception e) {
                    log.error("Unable to clean chat stack\n" + e);
                }
            }
            SendVideoNote videoNote = (SendVideoNote) t;
            try {
                Message message = execute(videoNote);
//                storeMsg(chatId, message.getMessageId());
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Send Video Note\n"+e);
            }
        } else if (t instanceof SendVoice) {

        }  else if (t instanceof PinChatMessage) {
            PinChatMessage pin = (PinChatMessage) t;
            try {
                boolean execute = execute(pin);
            } catch (TelegramApiException e) {
                log.error("Unable to execute method Pin Chat Message\n"+e);
            }
        }
    }

    private void cleanStack(String chatId){
        DBUtils db = new DBUtils();
        List<Integer> removeList = db.msgList(chatId);

        if (removeList.size() > 0){
            for (Integer id : removeList) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId);
                deleteMessage.setMessageId(id);
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    log.warn("Unable to execute method Delete Message\n"+e);
                }
            }
        }

        db.cleanMsgList(chatId);
        db.connectionClose();
    }

    private void storeMsg(String chatId, Integer messageId){
        DBUtils db = new DBUtils();
        db.storeMessage(chatId, messageId);
        db.connectionClose();
    }
}
