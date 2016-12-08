package attatrol.neural.ui.javafx.misc;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

/**
 * Ui combo box filled with {@link AnalyticalProcessorUiFactory}.
 * Use {@link #getResult()} to access chosen analytical processor.
 * @author attatrol
 *
 * @param <V> generated value type
 */
public class FactoryComboBox<V> extends ComboBox<AbstractUiFactory<? extends V>> {

    private V result;

    public FactoryComboBox() {
        this.setOnHidden(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                result = null;
                if (getValue() != null) {
                    result = getValue().generate();
                }
                else {
                    getSelectionModel().select(-1);
                    setValue(null);
                }
            }
        });
    }

    public V getResult() {
        return result;
    }

}
