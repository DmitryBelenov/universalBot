package bot.factory.handlers.impl.responses;

import bot.factory.handlers.ResponseMessage;
import bot.factory.handlers.impl.AliasMapManager;
import bot.factory.handlers.impl.MyWorldStates;
import bot.factory.handlers.interfaces.Response;
import bot.utils.DBUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class MyWorldDescriptionResponse implements Response {

    private Update update;

    MyWorldDescriptionResponse(Update update) {
        this.update = update;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke() {
        Message message = update.getMessage();
        String input = message.getText();

        String response = "OK, do you want keep your contact hidden?";

        Integer userId = message.getFrom().getId();
        ResponseMessage rm = new ResponseMessage();
        if (input.trim().equals("no") || (input.startsWith("yes ") && input.length() > 4)){
            boolean keepState = true;
            if (input.startsWith("yes ")){
                String description = input.substring(4);
                DBUtils db = new DBUtils();
                int res = db.addDescriptionToMyWorld(userId, description);
                db.connectionClose();

                if (res == -1){
                    response = "Hmm.. Looks like some problem\nTry again by /set_my_world command";
                    keepState = false;
                }
            }

            if (!keepState) {
                AliasMapManager.myWorldStatesMap.remove(userId);
            } else {
                AliasMapManager.myWorldStatesMap.put(userId, MyWorldStates.description_requested);
            }
            return (T) rm.fillMessage(update.getMessage(), response, keyboard());
        } else {
            response = "Incorrect input\nWrite me 'yes <pic description>' or 'no'";
            return (T) rm.fillMessage(update.getMessage(), response, true);
        }
    }

    private ReplyKeyboard keyboard(){
        InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("YES")
                .setCallbackData("yes"));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("NO")
                .setCallbackData("no"));

        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
