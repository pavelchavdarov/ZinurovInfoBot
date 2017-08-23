import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by Павел on 05.08.2017.
 */
public class Poster extends TimerTask {
    private InfoBot bot;
    private ReplyKeyboard repKeyboard;
    private Connection connection;

    public Poster() {
        try {
            this.connection = HikariCP.getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //    TODO: сделать конфигуратор клавиатуры
    private ReplyKeyboard createKeyboard(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keys = new ArrayList<KeyboardRow>();
        KeyboardRow keyRow = new KeyboardRow();
        keyRow.add(0,"Отписаться");
        keyRow.add(1, "Тренинг");
        keyRow.add(2, "Свалить");
        keys.add(keyRow);

        keyboardMarkup.setKeyboard(keys);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }


    public void run() {
        //TODO:
        //chatId и текст_сообщения нужно юрать из хранилища (DB?)
        PreparedStatement prepStatment = null;
        String chat = null;
        String postMessage = "";
        BigDecimal sub_id = null;
        BigDecimal post_id = null;
        System.out.println("работает таск");
        try {
            prepStatment = connection.prepareStatement("SELECT s.ID as sub_id, s.chat_id as chat, p.\"ID\" as post_id, p.\"Message\" as message\n" +
                    "FROM Subscribers s, \"Posts\" p\n" +
                    "WHERE current_date = s.subscribe_date + p.\"DayDelay\" and coalesce(s.last_post, -1) <> p.\"ID\"");
            ResultSet rs = prepStatment.executeQuery();

            while(rs.next()){
                chat = rs.getString("chat");
                postMessage = rs.getString("message");
                sub_id = rs.getBigDecimal("sub_id");
                post_id = rs.getBigDecimal("post_id");

//                System.out.println("Шлем сообщение '" + msg + "' в чат "+chat);
                bot.sendInlineMessageToChat(postMessage, Long.valueOf(chat));
                prepStatment = connection.prepareStatement("UPDATE subscribers SET last_post = ? where ID = ? ");
                prepStatment.setBigDecimal(1, post_id);
                prepStatment.setBigDecimal(2, sub_id);
                prepStatment.executeUpdate();
            }
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
