import DAO.HikariCP;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.function.LongFunction;

/**
 * Created by Павел on 05.08.2017.
 */
public class Poster extends TimerTask {
    private InfoBot bot;
    private ReplyKeyboard repKeyboard;
    private Connection connection;

    private int messagesCounter;

    Jedis jedis;

    public Poster() {
//        try {
//            this.connection = DAO.HikariCP.getDataSource().getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        messagesCounter = 0;

        URI redisURI = null;
        try {
            redisURI = new URI(System.getenv("REDIS_URL"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        jedis = new Jedis(redisURI);
    }

//    //    TODO: сделать конфигуратор клавиатуры
//    private ReplyKeyboard createKeyboard(){
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//
//        ArrayList<KeyboardRow> keys = new ArrayList<KeyboardRow>();
//        KeyboardRow keyRow = new KeyboardRow();
//        keyRow.add(0,"Отписаться");
//        keyRow.add(1, "Тренинг");
//        keyRow.add(2, "Свалить");
//        keys.add(keyRow);
//
//        keyboardMarkup.setKeyboard(keys);
//        keyboardMarkup.setResizeKeyboard(true);
//        return keyboardMarkup;
//    }


    public void run() {
        //TODO:
        //chatId и текст_сообщения нужно юрать из хранилища (DB?)
        PreparedStatement prepStatment = null;
        String chat = null;
        String postMessage = "";
        BigDecimal sub_id = null;
        BigDecimal post_id = null;
        BigDecimal botId = null;
        String messageButtons;

        try {
            this.connection = HikariCP.getDataSource().getConnection();
            if(!jedis.isConnected())
                jedis.connect();
            if (!jedis.hexists(bot.getBotUsername(), "botId")) {
                prepStatment = connection.prepareStatement("select id from bots where username = ?");
                prepStatment.setString(1, bot.getBotUsername());
                ResultSet rSet = prepStatment.executeQuery();
                if (rSet.next()) {
                    botId = rSet.getBigDecimal(1);
                    jedis.hset(bot.getBotUsername(), "botId", String.valueOf(botId));
                }

            }
            else
                botId = new BigDecimal(jedis.hget(bot.getBotUsername(), "botId").replaceAll(",", ""));


            prepStatment = connection.prepareStatement("SELECT s.ID as sub_id, s.chat_id as chat, p.id as post_id, p.message as message , p.buttons " +
                    "FROM Subscribers s, posts p " +
                    "WHERE p.ref_bot = ? and s.ref_bot = p.ref_bot and current_date = s.subscribe_date + p.daydelay and position('#'||p.id||'#' in coalesce(s.last_post, '')) = 0");
            prepStatment.setBigDecimal(1, botId);
            ResultSet rs = prepStatment.executeQuery();

            while(rs.next()){

                messagesCounter += 1;
                if(messagesCounter >= 10){
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    messagesCounter = 0;
                }

                chat = rs.getString("chat");
                postMessage = rs.getString("message");
                sub_id = rs.getBigDecimal("sub_id");
                post_id = rs.getBigDecimal("post_id");
                messageButtons = rs.getString("buttons");

//                System.out.println("Шлем сообщение '" + msg + "' в чат "+chat);
                bot.sendInlineMessageToChat(postMessage, messageButtons, Long.valueOf(chat));
                System.out.println("message: '"+postMessage+"' -> " + chat);
                prepStatment = connection.prepareStatement("UPDATE subscribers SET last_post = coalesce(last_post, '')||'#'||?||'#' where ID = ? ");
                prepStatment.setBigDecimal(1, post_id);
                prepStatment.setBigDecimal(2, sub_id);
                prepStatment.executeUpdate();

                String name = " ";
                prepStatment = connection.prepareStatement("select subscriber_name from subscribers where chat_id = ?");
                prepStatment.setBigDecimal(1, BigDecimal.valueOf(Long.valueOf(chat)));
                ResultSet resultSet = prepStatment.executeQuery();
                if(resultSet.next()){
                    name = resultSet.getString(1);
                }

                prepStatment = connection.prepareStatement("insert into posting_statistic (ref_post_id, ref_chat_id, ref_bot, subscriber_name, send_date) " +
                        "values(?, ?, ?, ?, current_timestamp)");
                prepStatment.setBigDecimal(1, post_id);
                prepStatment.setBigDecimal(2,BigDecimal.valueOf(Long.valueOf(chat)));
                prepStatment.setBigDecimal(3, botId);
                prepStatment.setString(4, name);
                prepStatment.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public InfoBot getBot() {
        return bot;
    }

    public void setBot(InfoBot bot) {
        this.bot = bot;
    }
}
