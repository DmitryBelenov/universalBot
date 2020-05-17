package bot.factory.handlers.impl.responses;

import bot.factory.handlers.impl.responses.MyWorldDescriptionResponse;
import bot.factory.handlers.interfaces.Response;
import bot.factory.handlers.interfaces.ResponseFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyWorldDescriptionResponseFactory implements ResponseFactory {

    private Update update;

    public MyWorldDescriptionResponseFactory(Update update) {
        this.update = update;
    }

    @Override
    public Response getFactory() {
        return new MyWorldDescriptionResponse(update);
    }
}
