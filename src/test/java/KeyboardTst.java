import MessageKeyboard.Button;
import MessageKeyboard.ButtonTypes;
import MessageKeyboard.Keyboard;
import MessageKeyboard.KeyboardTypes;
import com.google.gson.Gson;

public class KeyboardTst {
    public static void main(String[] args){
        Keyboard inlineKeyboard = new Keyboard(KeyboardTypes.Inline);
        Gson gson = new Gson();


        Button btn = Button.createButton()
                .setBtnTypes(ButtonTypes.Web)
                .setCaption("Тупичок Гоблина")
                .setValue("https://oper.ru");

        inlineKeyboard.getKeyboard().add(btn);

        btn = Button.createButton()
            .setBtnTypes(ButtonTypes.Callback)
            .setCaption("Команда")
            .setValue("test command");

        inlineKeyboard.getKeyboard().add(btn);

        btn = Button.createButton()
            .setBtnTypes(ButtonTypes.Like)
            .setCaption("Годится!")
            .setValue("LikeIt");

        inlineKeyboard.getKeyboard().add(btn);

        String gsonString = gson.toJson(inlineKeyboard);

        System.out.println(gsonString);

    }
}
