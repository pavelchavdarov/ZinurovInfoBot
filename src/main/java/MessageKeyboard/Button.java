package MessageKeyboard;

import javafx.scene.control.ButtonType;

public class Button {
    private String caption;
    private ButtonTypes btnType;
    private String value;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public ButtonTypes getBtnType() {
        return btnType;
    }

    public void setBtnTypes(ButtonTypes btnType) {
        this.btnType = btnType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
