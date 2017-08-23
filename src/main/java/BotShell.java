import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Павел on 05.08.2017.
 */
public class BotShell {
    public static void main(String[] args){
        ApiContextInitializer.init();
        TelegramBotsApi botApi = new TelegramBotsApi();
        try{
            InfoBot bot = new InfoBot();
            botApi.registerBot(bot);

            TimerTask timerTask = new MessagerBuilder().setBot(bot).createMessager();
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(timerTask, 0, 60*1000);

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
