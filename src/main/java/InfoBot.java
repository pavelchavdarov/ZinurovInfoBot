import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InfoBot extends TelegramLongPollingBot {
    private final String BotToken = "394479438:AAGgxN1tqUZgrtJVqe619tTetgJh4awZO6w";
    private final String BotUsername = "ZinurovInfoPosting_bot";
    //private ReplyKeyboard repKeyboard;
    private Connection connection;
    InfoBot() {
        super();
        //repKeyboard = createKeyboard();

        // установка соединения с базой
        try {
            connection = HikariCP.getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
/*
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String url = "jdbc:postgresql://127.0.0.1:5432/postgres";
        //Имя пользователя БД
        String name = "user1";
        //Пароль
        String password = "1";
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Драйвер PostgreSQL подключен");
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Соединение установлено");

        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }

    private void addSubscriber(CallbackQuery callbackQuery){
        PreparedStatement prepStatment;
        long chatId = callbackQuery.getMessage().getChatId();
        System.out.println("chatId: " + chatId);

        try {
            prepStatment = connection.prepareStatement("select exists(select true from subscribers  where chat_id = ?) res");
            prepStatment.setLong(1, chatId);
            ResultSet rs = prepStatment.executeQuery();
            while(rs.next()){
                if(!(rs.getBoolean("res"))){
                    prepStatment = connection.prepareStatement("INSERT INTO subscribers (chat_id, subscribe_date) VALUES (?, current_date)");
                    prepStatment.setBigDecimal(1, BigDecimal.valueOf(chatId));
                    int res = prepStatment.executeUpdate();
                    System.out.println("Добавлено " + res + " записей");
                }
                else {
                    System.out.println("Собеседник " + chatId + " уже есть в базе");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                            .setCallbackQueryId(callbackQuery.getId())
                            .setText("Вы подписаны на инфо-рассылку!")
                            .setShowAlert(true);
        try {
            this.sendApiMethod(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        sendInlineMessageToChat("Пока сюда будет валиться всякая тестовая байда\nпо расписанию", chatId);

    }

    private void addSubscriber(Message pMessage){
        PreparedStatement prepStatment;
        long chatId = pMessage.getChatId();
        System.out.println("chatId: " + chatId);

        try {
            prepStatment = connection.prepareStatement("select exists(select true from subscribers  where chat_id = ?) res");
            prepStatment.setLong(1, chatId);
            ResultSet rs = prepStatment.executeQuery();
            while(rs.next()){
                if(!(rs.getBoolean("res"))){
                    prepStatment = connection.prepareStatement("INSERT INTO subscribers (chat_id, subscribe_date) VALUES (?, current_date)");
                    prepStatment.setLong(1, chatId);
                    int res = prepStatment.executeUpdate();
                    System.out.println("Добавлено " + res + " записей");
                }
                else {
                    System.out.println("Собеседник " + chatId + " уже есть в базе");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keys = new ArrayList<>();
        KeyboardRow keyRow = new KeyboardRow();
        keyRow.add(0,"Управление подпиской");
        keyRow.add(1, "О боте");
        keyRow.add(2, "Контакты");
        keys.add(keyRow);

        keyboardMarkup.setKeyboard(keys);
        keyboardMarkup.setResizeKeyboard(true);

        sendMessageToChat("Вы подписаны на инфо-рассылку!", chatId, keyboardMarkup);
        sendInlineMessageToChat("Пока сюда будет валиться всякая тестовая байда\nпо расписанию", chatId);

    }

     private void removeSubscriber(CallbackQuery callbackQuery){
        PreparedStatement prepStatment;
        long chatId = callbackQuery.getMessage().getChatId();

        try {
            prepStatment = connection.prepareStatement("DELETE FROM subscribers WHERE  chat_id = ?");
            prepStatment.setBigDecimal(1, BigDecimal.valueOf(chatId));
            prepStatment.executeUpdate();
            System.out.println("Клиент " + chatId + " отписался.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQuery.getId())
                .setText("Вы отписались от рассылки \uD83D\uDC4B")
                .setShowAlert(true);

        try {
            this.sendApiMethod(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

//        clientGreetings(callbackQuery.getMessage());

         ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

         ArrayList<KeyboardRow> keys = new ArrayList<>();
         KeyboardRow keyRow = new KeyboardRow();
         keyRow.add(0,"Подписаться");
         keyRow.add(1, "О боте");

         keys.add(keyRow);

         keyboardMarkup.setKeyboard(keys);
         keyboardMarkup.setResizeKeyboard(true);

         sendMessageToChat("Вы отписались от рассылки, но в любой момент можете " +
                 "подписаться снова!", chatId, keyboardMarkup);
    }

    private void clientGreetings(Message pMessage){
/*
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        ArrayList<InlineKeyboardButton> buttonRow = new ArrayList<>();
        InlineKeyboardButton callbackBtn = new InlineKeyboardButton("Подписаться").setCallbackData("subscribe");
        buttonRow.add(callbackBtn);
        buttons.add(buttonRow);

        buttonRow = new ArrayList<>();
        callbackBtn = new InlineKeyboardButton("Zinurov.ru").setUrl("http://zinurov.ru");
        buttonRow.add(callbackBtn);
        buttons.add(buttonRow);

        inlineKeyboardMarkup.setKeyboard(buttons);
*/
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keys = new ArrayList<>();
        KeyboardRow keyRow = new KeyboardRow();
        keyRow.add(0,"Подписаться");
        keyRow.add(1, "О боте");
//        keyRow.add(2, "Свалить");
        keys.add(keyRow);

        keyboardMarkup.setKeyboard(keys);
        keyboardMarkup.setResizeKeyboard(true);

        SendMessage message = new SendMessage()
                .setChatId(pMessage.getChatId())
                .setText("Доброе время, " + pMessage.getFrom().getFirstName() + "!\nРад преветствовать тебя в моем боте.\nhttp://zinurov.ru")
                .setReplyMarkup(keyboardMarkup);
        try {
            this.sendApiMethod(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


    public void onUpdateReceived(Update update) {
        String command, textCommand;
        Message msg;
        CallbackQuery callbackQuery;
        if(update.hasMessage()){
            msg = update.getMessage();
            System.out.println(msg.getText());
            if(msg.isCommand()){
                command = msg.getText();
                System.out.println("command: " + command);
                switch (command) {
                    case "/start":          clientGreetings(msg);
                                            break;
//                    case "/Подписаться":    addSubscriber(msg);
//                                            break;
//                    case "/Отписаться":     removeSubscriber(msg);
//                        break;
                    }
            }
            else if(msg.hasText()){
                textCommand = msg.getText();
                System.out.println("command: " + textCommand);
                switch (textCommand) {
                    case "Подписаться":    addSubscriber(msg);
                                            break;
//                    case "Управление подпиской":    addSubscriber(msg);
//                        break;
//                    case "О боте":    addSubscriber(msg);
//                        break;
//                    case "Контакты":    addSubscriber(msg);
//                        break;
                }
            }

        }
        else if(update.hasCallbackQuery()){
            callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            switch (data){
                case "subscribe":   addSubscriber(callbackQuery);
                                    break;
                case "unsubscribe": removeSubscriber(callbackQuery);
            }

        }


    }

    void sendInlineMessageToChat(String pMessage, long chatId){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        ArrayList<InlineKeyboardButton> buttonRow = new ArrayList<>();
        InlineKeyboardButton callbackBtn = new InlineKeyboardButton("www.Zinurov.ru").setUrl("http://zinurov.ru");
        buttonRow.add(callbackBtn);
        //buttons.add(buttonRow);

        //buttonRow = new ArrayList<>();
        callbackBtn = new InlineKeyboardButton("@Zinurovru").setUrl("https://t.me/zinurovru");
        buttonRow.add(callbackBtn);
        //buttons.add(buttonRow);

//        buttonRow = new ArrayList<>();
        callbackBtn = new InlineKeyboardButton("Отписаться").setCallbackData("unsubscribe");
        buttonRow.add(callbackBtn);
        buttons.add(buttonRow);

        inlineKeyboardMarkup.setKeyboard(buttons);

        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(pMessage)
                .enableHtml(true)
                .setReplyMarkup(inlineKeyboardMarkup);
        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    /*
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keys = new ArrayList<>();
        KeyboardRow keyRow = new KeyboardRow();
        keyRow.add(0,"Отписаться");
        keyRow.add(1, "Тренинг");
        keyRow.add(2, "Свалить");
        keys.add(keyRow);

        keyboardMarkup.setKeyboard(keys);
        keyboardMarkup.setResizeKeyboard(true);

        message = new SendMessage()
                .setChatId(chatId)
                .setText("qwe")
                .setReplyMarkup(keyboardMarkup);
        try {
            this.sendApiMethod(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    */
    }

    void sendMessageToChat(String pMessage, long chatId, ReplyKeyboard keyboard){
/*
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        ArrayList<InlineKeyboardButton> buttonRow = new ArrayList<>();
        InlineKeyboardButton callbackBtn = new InlineKeyboardButton("Zinurov.ru").setUrl("http://zinurov.ru");
        buttonRow.add(callbackBtn);
        buttons.add(buttonRow);

        buttonRow = new ArrayList<>();
        callbackBtn = new InlineKeyboardButton("Связаться с автором").setUrl("https://t.me/zinurovru");
        buttonRow.add(callbackBtn);
        buttons.add(buttonRow);

        buttonRow = new ArrayList<>();
        callbackBtn = new InlineKeyboardButton("Отписаться").setCallbackData("unsubscribe");
        buttonRow.add(callbackBtn);
        buttons.add(buttonRow);

        inlineKeyboardMarkup.setKeyboard(buttons);
*/
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keys = new ArrayList<>();
        KeyboardRow keyRow = new KeyboardRow();
        keyRow.add(0,"Управление подпиской");
        keyRow.add(1, "О боте");
        keyRow.add(2, "Контакты");
        keys.add(keyRow);

        keyboardMarkup.setKeyboard(keys);
        keyboardMarkup.setResizeKeyboard(true);

        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(pMessage)
                .setReplyMarkup(keyboard);
        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    /*
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keys = new ArrayList<>();
        KeyboardRow keyRow = new KeyboardRow();
        keyRow.add(0,"Отписаться");
        keyRow.add(1, "Тренинг");
        keyRow.add(2, "Свалить");
        keys.add(keyRow);

        keyboardMarkup.setKeyboard(keys);
        keyboardMarkup.setResizeKeyboard(true);

        message = new SendMessage()
                .setChatId(chatId)
                .setText("qwe")
                .setReplyMarkup(keyboardMarkup);
        try {
            this.sendApiMethod(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    */
    }

    public String getBotUsername() {
        return BotUsername;
    }

    public String getBotToken() {
        return BotToken;
    }
}
