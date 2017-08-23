import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.Calendar;
import java.util.Date;
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

            TimerTask postScript = new PosterBuilder().setBot(bot).createPoster();
            Timer timer = new Timer(true);
            Calendar cal =  Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY,9);
            cal.set(Calendar.MINUTE,30);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);

            Date time = cal.getTime();

            timer.scheduleAtFixedRate(postScript, time, 12 * 60 * 60 * 1000);



        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
