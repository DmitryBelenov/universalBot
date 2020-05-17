package bot.factory.handlers.impl.responses;

import bot.factory.handlers.impl.responses.MyWorldHideContactResponse;
import bot.factory.handlers.interfaces.Response;
import bot.factory.handlers.interfaces.ResponseFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyWorldHideContactResponseFactory implements ResponseFactory {

    private Update update;

    public MyWorldHideContactResponseFactory(Update update) {
        this.update = update;
    }

    @Override
    public Response getFactory() {
        return new MyWorldHideContactResponse(update);
    }
}
