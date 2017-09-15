import DAO.BotFunctions;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

public class BotConfigurator extends TimerTask {
    private Connection connection;
    private List<InfoBot> botList;
    private Timer timer;
    public void setBotList(List<InfoBot> botList) {
        this.botList = botList;
    }



    public BotConfigurator(){
        timer = new Timer(true);
    }

    @Override
    public void run() {
        ApiContextInitializer.init();
        TelegramBotsApi botApi = new TelegramBotsApi();
        InfoBot bot;
        List<InfoBot> bList;
        for (InfoBot b : botList){
            HashMap<String, String> settings = DAO.BotFunctions.getBotSettings(b.getBotUsername());
            b.setBotSettings(settings);
        }

        List<HashMap<String, String>> botSettingsListNew = BotFunctions.getAllBots();
        for(HashMap<String, String> setting : botSettingsListNew){
            bList = botList.stream().filter(b -> b.getBotUsername().equals(setting.get("BotUserName"))).collect(Collectors.toList());
            if(bList.isEmpty()){
                bot = new InfoBot(setting);
                try {
                    botApi.registerBot(bot);
                } catch (TelegramApiRequestException e) {
                    e.printStackTrace();
                }
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
        }
    }
}
