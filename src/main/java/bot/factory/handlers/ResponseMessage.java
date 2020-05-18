package bot.factory.handlers;

import bot.factory.handlers.impl.commands.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ResponseMessage {
    public ResponseMessage() {
    }

    public SendMessage fillMessage(Message msg, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(msg.getChatId());
        message.setText(text);

        return message;
    }

    public SendMessage fillMessage(Message msg, String text, boolean defaultButtons) {
        if (defaultButtons) {
            SendMessage message = new SendMessage();
            message.setChatId(msg.getChatId());
            message.setText(text);
            message.setReplyMarkup(keyboard());

            return message;
        } else return fillMessage(msg, text);
    }

    public SendMessage fillMessage(Message msg, String text, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(msg.getChatId());
        message.setText(text);
        message.setReplyMarkup(keyboard);

        return message;
    }

    private ReplyKeyboard keyboard(){
        InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();

        // первый ряд кнопок
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("YO!")
                .setCallbackData(YoLookAroundCommand.alias));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("INFO")
                .setCallbackData(InfoCommand.alias));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("MW")
                .setCallbackData(MyWorldCommand.alias));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("SET MW")
                .setCallbackData(MyWorldSetCommand.alias));

        // второй ряд кнопок
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("V NOTES")
                .setCallbackData(VideoNotesCommand.alias));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("SET V NOTE")
                .setCallbackData(VideoNoteSetRequestCommand.alias));

        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
