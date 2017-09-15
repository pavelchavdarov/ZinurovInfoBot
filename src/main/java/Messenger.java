import DAO.HikariCP;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.TimerTask;

public class Messenger extends TimerTask {
    private InfoBot bot;
    private Connection connection;

    public Messenger() {
//        try {
//            this.connection = DAO.HikariCP.getDataSource().getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void run() {
        PreparedStatement prepStatment = null;
        String chat = null;
        String message = "";
        BigDecimal messageId = null;
        BigDecimal post_id = null;
        try {
            this.connection = HikariCP.getDataSource().getConnection();
            prepStatment = connection.prepareStatement("SELECT sub.chat_id chat_id, msg.message  message " +
                    "FROM messages msg, subscribers sub , bots bot " +
                    "WHERE bot.username = ? and msg.ref_bot = bot.id and sub.ref_bot = bot.id and msg.date_to_send = current_date and not(is_sent)");
            prepStatment.setString(1, bot.getBotUsername());
            ResultSet rs = prepStatment.executeQuery();

            while(rs.next()){
//                messageId = rs.getBigDecimal("msg_id");
                chat = rs.getString("chat_id");
                message = rs.getString("message");
//                System.out.println("Шлем сообщение '" + msg + "' в чат "+chat);
                bot.sendInlineMessageToChat(message, Long.valueOf(chat));
            }
            prepStatment = connection.prepareStatement("UPDATE messages set is_sent = true where date_to_send = current_date");
            prepStatment.executeUpdate();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setBot(InfoBot bot) {
        this.bot = bot;
    }
}
