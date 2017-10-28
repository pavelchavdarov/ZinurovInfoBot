package MessageKeyboard;

import javafx.scene.control.ButtonType;

public class Button {
    private String caption;
    private ButtonTypes btnType;
    private String value;

    public static Button createButton() {
        return new Button();
    }

    public String getCaption() {
        return caption;
    }

    public Button setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public ButtonTypes getBtnType() {
        return btnType;
    }

    public Button setBtnTypes(ButtonTypes btnType) {
        this.btnType = btnType;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Button setValue(String value) {
        this.value = value;
        return this;
    }

    private Button() {
    }
}
