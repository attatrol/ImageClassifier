package attatrol.neural.ui.javafx.misc;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class UiUtils {

    private UiUtils() { }

    /**
     * Shows some error message to user.
     * @param message message
     */
    public static void showTestMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
