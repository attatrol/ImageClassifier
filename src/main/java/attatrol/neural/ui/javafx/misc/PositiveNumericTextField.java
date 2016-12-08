package attatrol.neural.ui.javafx.misc;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

/**
 * Text field that accepts only numeric input.
 * @author attatrol
 */
public class PositiveNumericTextField extends TextField {

    public PositiveNumericTextField() {
        super();

        UnaryOperator<Change> filter = change -> {
            String text = change.getText();
            if (text.matches("\\d*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        this.setTextFormatter(textFormatter);
    }
}
