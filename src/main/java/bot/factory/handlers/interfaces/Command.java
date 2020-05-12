package bot.factory.handlers.interfaces;

public interface Command {
    <T>T invoke();
    String getAlias();
}
