package attatrol.neural.ui.javafx.misc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Text field that accepts only symbols used in record of positive float
 * point numbers.
 * @author attatrol
 */
public class PositiveDoubleTextField extends TextField {

    public PositiveDoubleTextField() {
        super();

        PositiveDoubleTextField link = this;
        this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue
                        .matches("|\\.|\\d+\\.?|\\d+\\.?\\d*|\\d*\\.?\\d+")) {
                    link.setText(oldValue);
                }
            }
        });
    }
}
