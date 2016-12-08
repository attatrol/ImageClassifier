package attatrol.neural.ui.javafx.misc;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class PositiveDoubleReturnDialog extends GenericValueReturnDialog<Double>{

    private PositiveDoubleTextField textField = new PositiveDoubleTextField();

    /**
     * Default ctor.
     * @param title
     * @param label
     */
    public PositiveDoubleReturnDialog(String title, String label) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(new Label(label), 0, 0);
        grid.add(textField, 0, 1);
        this.getDialogPane().setContent(grid);
        this.setTitle(title);
    }

    @Override
    protected Double createResult() {
        return Double.parseDouble(textField.getText());
    }

    @Override
    protected void validate() throws Exception {
        try {
            Double.parseDouble(textField.getText());
        }
        catch (NumberFormatException ex) {
            throw new IllegalStateException("Bad float number format:" + ex.getLocalizedMessage());
        }
    }

}
