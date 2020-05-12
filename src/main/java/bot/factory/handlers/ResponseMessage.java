package bot.factory.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ResponseMessage {
    public ResponseMessage() {
    }

    public SendMessage fillMessage(Message msg, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(msg.getChatId());
        message.setText(text);
        return message;
    }
}
