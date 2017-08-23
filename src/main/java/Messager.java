import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by Павел on 05.08.2017.
 */
public class Messager extends TimerTask {
    private InfoBot bot;
    private ReplyKeyboard repKeyboard;
    private Connection connection;

    public Messager() {
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
        String msg = "";
        System.out.println("работает таск");
        try {
            prepStatment = connection.prepareStatement("select s.chat_id chat, m.msg msg " +
                    "from subscribers s, messages m " +
                    "where current_timestamp - (s.reg_date + m.timeshift) > '0 minute'::interval " +
                    "and current_timestamp - (s.reg_date + m.timeshift) < '1 minute'::interval");
            ResultSet rs = prepStatment.executeQuery();

            while(rs.next()){
                chat = rs.getString("chat");
                msg = rs.getString("msg");
//                System.out.println("Шлем сообщение '" + msg + "' в чат "+chat);
                bot.sendInlineMessageToChat(msg, Long.valueOf(chat));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (chat != null) {
            SendMessage message = new SendMessage()
                    .setChatId(Long.valueOf(chat))
                    .setText(msg)
                    .setReplyMarkup(repKeyboard);
        }
    }

    public InfoBot getBot() {
        return bot;
    }

    public void setBot(InfoBot bot) {
        this.bot = bot;
    }
}
