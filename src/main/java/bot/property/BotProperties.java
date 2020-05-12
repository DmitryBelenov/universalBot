package bot.property;

import bot.BotStarter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BotProperties {

    private static Logger log = Logger.getLogger(BotProperties.class);

    private static Properties properties;
    private final String propertyPath = System.clearProperty("user.home") + "/AppData/Local/UBServer/";

    public BotProperties() {
    }

    public boolean initialize() {
        File propFile = new File(propertyPath+"ubot.properties");

        if (!propFile.exists()) {
            log.error("Property file not exists");
            return false;
        }

        properties = new Properties();
        try {
            properties.load(new FileInputStream(propFile.getAbsolutePath()));
        } catch (IOException e) {
           log.error("Properties loading error\n"+e);
           return false;
        }

        log.info("Properties loaded successfully");
        return true;
    }

    public static synchronized Properties get(){
        return properties;
    }
}
