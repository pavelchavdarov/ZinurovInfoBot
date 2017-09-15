import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.*;

/**
 * Created by Павел on 05.08.2017.
 */
public class BotShell {
    public static void main(String[] args){
        List<HashMap<String, String>> botSettingsList;
        List<InfoBot> botList = new ArrayList<>();
        InfoBot bot;

        ApiContextInitializer.init();
        TelegramBotsApi botApi = new TelegramBotsApi();
        Timer timer = new Timer(true);
        try{
            botSettingsList = DAO.BotFunctions.getAllBots();
            for (HashMap<String, String> botSettings : botSettingsList) {
                bot = new InfoBot(botSettings);
                botApi.registerBot(bot);
                botList.add(bot);
                TimerTask postScript = new PosterBuilder().setBot(bot).createPoster();

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 9);
                cal.set(Calendar.MINUTE, 30);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                Date time = cal.getTime();

                timer.scheduleAtFixedRate(postScript, 0, 60 * 60 * 1000);

                TimerTask messageScript = new MessengerBuilder().setBot(bot).createMessenger();

                timer.scheduleAtFixedRate(messageScript, 0, 60 * 1000);

            }
            TimerTask config = new BotConfiguratorBuilder().setBotList(botList).createBotConfigurator();
            timer.scheduleAtFixedRate(config, 0, 60 * 1000);

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
