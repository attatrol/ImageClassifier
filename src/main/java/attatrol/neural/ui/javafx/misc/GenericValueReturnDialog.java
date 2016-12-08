package attatrol.neural.ui.javafx.misc;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Abstract class for any dialog that supposed to return value.
 * @author attatrol
 *
 * @param <T> type of the return value
 */
public abstract class GenericValueReturnDialog<T> extends Dialog<T> {

    public static final ButtonType OK_BUTTON_TYPE = new ButtonType("OK", ButtonData.OK_DONE);

    /**
     * Default constructor
     */
    public GenericValueReturnDialog() {
        getDialogPane().getButtonTypes().addAll(OK_BUTTON_TYPE, ButtonType.CANCEL);
        setResultConverter(dialogButton -> {
            if (dialogButton == OK_BUTTON_TYPE) {
                return createResult();
            }
            return null;
        });
        final Button btOk = (Button) getDialogPane().lookupButton(OK_BUTTON_TYPE);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                validate();
            }
            catch (Exception ex) {
                event.consume();
                UiUtils.showTestMessage(ex.getLocalizedMessage());
            }
        });
        // getDialogPane().setContent(someNode);
    }

    /**
     * Produces resulting object from internal state of this dialog
     * @return resulting object
     */
    protected abstract T createResult();

    /**
     * Checks if state of this dialog permits result to be generated.
     * @throws Exception if internal state of this dialog makes impossible to produce a valid result.
     */
    protected abstract void validate() throws Exception;

}
