
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

public class InfoBot extends TelegramLongPollingBot {
    private String BotToken;// = "394479438:AAGgxN1tqUZgrtJVqe619tTetgJh4awZO6w";
    private String BotUsername;// = "ZinurovInfoPosting_bot";
    private BotSession session;

    public HashMap<String, String> getBotSettings() {
        return botSettings;
    }

    public void setBotSettings(HashMap<String, String> botSettings) {
        this.botSettings = botSettings;
    }

    private HashMap<String, String> botSettings;
    //private ReplyKeyboard repKeyboard;
    //private Connection connection;

   // private int messagesCounter;
    public InfoBot() {
        super();

     //   messagesCounter = 0;
        //repKeyboard = createKeyboard();

        // установка соединения с базой
//        try {
//            connection = DAO.HikariCP.getDataSource().getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public InfoBot(HashMap<String, String> settings) {
        super();
        botSettings = settings;
    }

    private void addSubscriber(CallbackQuery callbackQuery){
        //PreparedStatement prepStatment;
        Long chatId = callbackQuery.getMessage().getChatId();
        String subscriberName = callbackQuery.getMessage().getFrom().getFirstName() + " " + callbackQuery.getMessage().getFrom().getLastName();
        String phoneNumber = callbackQuery.getMessage().getContact().getPhoneNumber();
        int userId = callbackQuery.getMessage().getContact().getUserID();
        System.out.println("chatId: " + chatId);
        /*
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
        */
        DAO.BotFunctions.AddSubscriber(chatId, subscriberName, phoneNumber, getBotUsername());
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQuery.getId())
                .setText("Вы подписаны на инфо-рассылку!")
                .setShowAlert(true);
        try {
            this.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        sendInlineMessageToChat("Пока сюда будет валиться всякая тестовая байда\nпо расписанию", chatId);

    }

    private void addSubscriber(Message pMessage){
        //PreparedStatement prepStatment;
        long chatId = pMessage.getChatId();
        String subscriberName = pMessage.getFrom().getFirstName() + " " +pMessage.getFrom().getLastName();
        String phoneNumber = null; //pMessage.getContact().getPhoneNumber();
        int userId = 0; //pMessage.getContact().getUserID();
        System.out.println("chatId: " + chatId);
        System.out.println("subscriberName: " + subscriberName);
        System.out.println("phoneNumber: " + phoneNumber);
        System.out.println("userId: " + userId);
        /*
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
        */
        DAO.BotFunctions.AddSubscriber(chatId, subscriberName,phoneNumber, getBotUsername());

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keys = new ArrayList<>();
        KeyboardRow keyRow = new KeyboardRow();
        keyRow.add(0,"⚙️Управление подпиской");
        keys.add(keyRow);
        keyRow = new KeyboardRow();
        keyRow.add(0, "❓ О боте");
        keyRow.add(1, "\uD83D\uDCDE Контакты");
        keys.add(keyRow);

        keyboardMarkup.setKeyboard(keys);
        keyboardMarkup.setResizeKeyboard(true);

        sendMessageToChat("Вы подписаны на инфо-рассылку!", chatId, keyboardMarkup);
        //sendInlineMessageToChat("Пока сюда будет валиться всякая тестовая байда\nпо расписанию", chatId);
        sendInlineMessageToChat(botSettings.get("BotSubscribePhrase"), chatId);

    }

    private void removeSubscriber(CallbackQuery callbackQuery){
        //PreparedStatement prepStatment;
        long chatId = callbackQuery.getMessage().getChatId();
        /*
        try {
            prepStatment = connection.prepareStatement("DELETE FROM subscribers WHERE  chat_id = ?");
            prepStatment.setBigDecimal(1, BigDecimal.valueOf(chatId));
            prepStatment.executeUpdate();
            System.out.println("Клиент " + chatId + " отписался.");
        } catch (Exception e) {
            e.printStackTrace();
        }
		*/
        String msg = botSettings.get("BotBayPhrase");
        String name = DAO.BotFunctions.getSubscriberName(chatId);
        msg = msg.replaceAll("\\[имя пользователя]", name);

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQuery.getId())
                //.setText("Вы отписались от рассылки \uD83D\uDC4B")
                .setText(msg)
                .setShowAlert(true);

        DAO.BotFunctions.RemoveSubscriber(chatId, getBotUsername());
        try {
            this.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

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
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keys = new ArrayList<>();
        KeyboardRow keyRow = new KeyboardRow();
//        keyRow.add(0,"Подписаться");
        KeyboardButton btn = new KeyboardButton();
        btn.setText("Подписаться1");
        //btn.setRequestContact(true);
        keyRow.add(0,"Подписаться");
        keyRow.add(1,btn);
        keyRow.add(2, "О боте");
        keys.add(keyRow);

        keyboardMarkup.setKeyboard(keys);
        keyboardMarkup.setResizeKeyboard(true);

        String name =  pMessage.getFrom().getFirstName() + " " + pMessage.getFrom().getLastName();//DAO.BotFunctions.getSubscriberName(pMessage.getChatId());
        System.out.println("name: " + name);
        String msg = botSettings.get("BotHelloPhrase");
        msg = msg.replaceAll("\\[имя пользователя]", name);

        SendMessage message = new SendMessage()
                .setChatId(pMessage.getChatId())
                //.setText("Доброе время, " + pMessage.getFrom().getFirstName() + "!\nРад преветствовать тебя в моем боте.\nhttp://zinurov.ru")
                .setText(msg)
                .setReplyMarkup(keyboardMarkup);
        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


    public void onUpdateReceived(Update update) {
        String command, textCommand;
        Message msg;
        botSettings = DAO.BotFunctions.getBotSettings(getBotUsername());
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
                    default: sendInlineMessageToChat(botSettings.get("BotDefaultAnswer"), msg.getChatId());


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
                    default:
                        String name = msg.getFrom().getFirstName() + " " + msg.getFrom().getLastName();
                        String msg_text = botSettings.get("BotDefaultAnswer");
                        msg_text = msg_text.replaceAll("\\[имя пользователя]", name);
                        sendInlineMessageToChat(msg_text, msg.getChatId());
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

        //callbackBtn = new InlineKeyboardButton("@Zinurovru").setUrl("https://t.me/zinurovru");
        callbackBtn = new InlineKeyboardButton("@" + botSettings.get("BotAuthor")).setUrl("https://t.me/" + botSettings.get("BotAuthor"));
        buttonRow.add(callbackBtn);

        callbackBtn = new InlineKeyboardButton("Отписаться").setCallbackData("unsubscribe");
        buttonRow.add(callbackBtn);
        buttons.add(buttonRow);

        inlineKeyboardMarkup.setKeyboard(buttons);
        String name = DAO.BotFunctions.getSubscriberName(chatId);
        System.out.println("name: " + name);
        pMessage = pMessage.replaceAll("\\[имя пользователя]", name);

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

//        messagesCounter += 1;
//        if(messagesCounter >= 15){
//            try {
//                wait(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            messagesCounter = 0;
//        }
    }



    void sendMessageToChat(String pMessage, long chatId, ReplyKeyboard keyboard){

        String name = DAO.BotFunctions.getSubscriberName(chatId);
        System.out.println(name);
        pMessage = pMessage.replaceAll("\\[имя пользователя]", name);

        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(pMessage)
                .setReplyMarkup(keyboard);
        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }



    public String getBotUsername() {
        return botSettings.get("BotUserName");
    }

    public String getBotToken() {
        return botSettings.get("BotToken");
    }

    public BotSession getSession() {
        return session;
    }

    public void setSession(BotSession session) {
        this.session = session;
    }

}
