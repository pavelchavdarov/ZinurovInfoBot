import java.util.HashMap;
import java.util.List;

public class BotConfiguratorBuilder {
    List<InfoBot> botList;

    public BotConfiguratorBuilder setBotList(List<InfoBot> botList){
        this.botList = botList;
        return this;
    }
    public BotConfigurator createBotConfigurator() {
        BotConfigurator botConf = new BotConfigurator();
        botConf.setBotList(this.botList);
        return botConf;
    }
}