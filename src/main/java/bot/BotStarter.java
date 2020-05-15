package bot;

import bot.property.BotProperties;
import bot.utils.SchedulesManager;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class BotStarter {
    private static Logger log = Logger.getLogger(BotStarter.class);

    public static void main(String[] args) {
        BotProperties properties = new BotProperties();
        if (properties.initialize()) {
            SchedulesManager sm = new SchedulesManager();
            sm.init();

            Frame f = new Frame();
            f.start();
        } else {
            log.warn("Properties loading needed");
        }
    }

    private static void startBot(JLabel label, JButton button) {
        try {
            label.setText("Initializing API context...");
            log.info("Initializing API context...");
            ApiContextInitializer.init();

            TelegramBotsApi botsApi = new TelegramBotsApi();

            label.setText("Configuring bot options...");
            log.info("Configuring bot options...");
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

            botOptions.setProxyHost(BotProperties.get().getProperty("proxy.host"));
            try {
                botOptions.setProxyPort(Integer.parseInt(BotProperties.get().getProperty("proxy.port")));
            } catch (NumberFormatException nfe){
                log.error("Unable parse proxy port number from properties\n"+nfe);
            }
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

            label.setText("Registering Bot...");
            log.info("Registering Bot...");
            botsApi.registerBot(new Bot(botOptions));

            log.info("Bot is ready for work!");

            label.setText("active from " + new Date());
            button.setEnabled(false);
        } catch (TelegramApiRequestException e) {
            log.error("Error while initializing bot!\n" + e);
            label.setText("Error while initializing bot!");
        }
    }

    private static class Frame extends JFrame {

        static boolean started = false;

        Frame() {
            setSize(300, 80);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setResizable(false);

            JPanel panel = new JPanel(new BorderLayout());
            JButton button = new JButton("Start Universal Bot");
            JLabel label = new JLabel("inactive..");

            panel.add(button, BorderLayout.NORTH);
            panel.add(label, BorderLayout.SOUTH);

            button.addActionListener(e -> {
                if (!Frame.started) {
                    Frame.started = true;
                    Thread th = new Thread(() -> BotStarter.startBot(label, button));
                    th.start();
                }
            });

            add(panel);
        }

        void start() {
            setVisible(true);
        }
    }
}
