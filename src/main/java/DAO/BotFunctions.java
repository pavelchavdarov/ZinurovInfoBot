package DAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BotFunctions {

    public static QueryResults AddSubscriber(Long chatId, String subscriberName, String phoneNumber, String botUserName) {
        PreparedStatement prepStatment;
        QueryResults result = null;
        Connection connection;
        BigDecimal botId = null;
        try {
            connection = HikariCP.getDataSource().getConnection();
            prepStatment = connection.prepareStatement("select id from bots where username = ?");
            prepStatment.setString(1, botUserName);
            ResultSet resultSet = prepStatment.executeQuery();
            if(resultSet.next())
                botId = resultSet.getBigDecimal(1);


            prepStatment = connection.prepareStatement("select exists(select true from subscribers  where chat_id = ? and ref_bot = ?) res");
            prepStatment.setBigDecimal(1, BigDecimal.valueOf(chatId));
            prepStatment.setBigDecimal(2, botId);
            ResultSet rs = prepStatment.executeQuery();
            if(rs.next()){
                if(!(rs.getBoolean("res"))){

                    prepStatment = connection.prepareStatement("INSERT INTO subscribers (chat_id, subscribe_date, subscriber_name, phone_number, ref_bot) VALUES (?, current_date, ?, ?, ?)");
                    prepStatment.setBigDecimal(1, BigDecimal.valueOf(chatId));
                    prepStatment.setString(2, subscriberName);
                    prepStatment.setString(3, phoneNumber);
                    prepStatment.setBigDecimal(4, botId);
                    prepStatment.executeUpdate();
                    result = QueryResults.Successful;

                    prepStatment = connection.prepareStatement("insert into subscribe_statistic (event_date, event, subscriber_name, ref_chat_id, ref_bot) VALUES (current_date, 1, ?, ?, ?)");
                    prepStatment.setString(1, subscriberName);
                    prepStatment.setBigDecimal(2, BigDecimal.valueOf(chatId));
                    prepStatment.setBigDecimal(3, botId);
                    prepStatment.executeUpdate();
                }
                else {
                    result = QueryResults.AllreadyExists;
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            result = QueryResults.Exception;
        }
        return result;
    }

    public static QueryResults RemoveSubscriber(Long chatId, String bonUserName) {
        PreparedStatement prepStatment;
        String subscriberName = " ";
        BigDecimal botId = null;
        QueryResults result = null;
        Connection connection;
        try {
            connection = HikariCP.getDataSource().getConnection();
            prepStatment = connection.prepareStatement("select subscriber_name from subscribers where chat_id = ?");
            prepStatment.setBigDecimal(1, BigDecimal.valueOf(chatId));
            ResultSet rs = prepStatment.executeQuery();
            if(rs.next()){
                subscriberName = rs.getString(1);
            }

            prepStatment = connection.prepareStatement("select id from bots where username = ?");
            prepStatment.setString(1,bonUserName);
            rs = prepStatment.executeQuery();
            if(rs.next()){
                botId = rs.getBigDecimal(1);
            }

            prepStatment = connection.prepareStatement("DELETE FROM subscribers WHERE  chat_id = ? and ref_bot = ?");
            prepStatment.setBigDecimal(1, BigDecimal.valueOf(chatId));
            prepStatment.setBigDecimal(2, botId);
            prepStatment.executeUpdate();
            result = QueryResults.Successful;

            prepStatment = connection.prepareStatement("insert into subscribe_statistic (event_date, event, subscriber_name, ref_chat_id, ref_bot) VALUES (current_date, 0, ?, ?, ?)");
            prepStatment.setString(1,subscriberName);
            prepStatment.setBigDecimal(2, BigDecimal.valueOf(chatId));
            prepStatment.setBigDecimal(3, botId);
            prepStatment.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = QueryResults.Exception;
        }
        return result;
    }

    public static String GetToken(String botUserName) {
        PreparedStatement prepStatment;
        String token = null;
        Connection connection;
        try {
            connection = HikariCP.getDataSource().getConnection();
            prepStatment = connection.prepareStatement("SELECT token FROM bots WHERE username = ?");
            prepStatment.setString(1, botUserName);
            ResultSet rs = prepStatment.executeQuery();
            if(rs.next()) {
                token = rs.getString(1);
            }
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public static List<HashMap<String, String>> getAllBots(){
        PreparedStatement prepStatment;
        HashMap<String, String> bot;
        ArrayList<HashMap<String, String>> botList = new ArrayList<HashMap<String, String>>();
        Connection connection;
        try {
            connection = HikariCP.getDataSource().getConnection();
            prepStatment = connection.prepareStatement("SELECT token, username, hello_phrase, bay_phrase, " +
                    "author_link, defaultanswer, subscribe_phrase from bots");
            ResultSet rs = prepStatment.executeQuery();
            while (rs.next()){
                bot = new HashMap<>();
                bot.put("BotUserName",rs.getString(2));
                bot.put("BotToken",rs.getString(1));
                bot.put("BotBayPhrase",rs.getString(4));
                bot.put("BotHelloPhrase",rs.getString(3));
                bot.put("BotAuthor",rs.getString(5));
                bot.put("BotDefaultAnswer",rs.getString(6));
                bot.put("BotSubscribePhrase",rs.getString(7));

                botList.add(bot);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return botList;
    }

    public static HashMap<String, String> getBotSettings(String botUserName){
        PreparedStatement prepStatment;
        HashMap<String, String> bot = null;
        Connection connection;
//        ArrayList<HashMap<String, String>> botList = new ArrayList<HashMap<String, String>>();
        try {
            connection = HikariCP.getDataSource().getConnection();
            prepStatment = connection.prepareStatement("SELECT token, username, hello_phrase, bay_phrase, " +
                    "author_link, defaultanswer, subscribe_phrase from bots where username = ?");
            prepStatment.setString(1,botUserName);
            ResultSet rs = prepStatment.executeQuery();
            if(rs.next()){
                bot = new HashMap<>();
                bot.put("BotUserName",rs.getString(2));
                bot.put("BotToken",rs.getString(1));
                bot.put("BotBayPhrase",rs.getString(4));
                bot.put("BotHelloPhrase",rs.getString(3));
                bot.put("BotAuthor",rs.getString(5));
                bot.put("BotDefaultAnswer",rs.getString(6));
                bot.put("BotSubscribePhrase",rs.getString(7));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bot;
    }

    public static void addPostingStatistic(){

    }


    public static String getSubscriberName(Long chatId){
        PreparedStatement prepStatment;
        Connection connection;
        QueryResults result = null;
        String name = " ";
        try {
            connection = HikariCP.getDataSource().getConnection();
            prepStatment = connection.prepareStatement("select subscriber_name from subscribers where chat_id = ?");
            prepStatment.setBigDecimal(1, BigDecimal.valueOf(chatId));
            ResultSet rs = prepStatment.executeQuery();
            if(rs.next()){
                name = rs.getString(1);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }
}
