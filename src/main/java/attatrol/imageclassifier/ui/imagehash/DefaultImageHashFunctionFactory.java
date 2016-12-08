package attatrol.imageclassifier.ui.imagehash;

import java.util.Optional;

import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;
import attatrol.imageclassifier.imagehash.DefaultImageHashFunction;
import attatrol.imageclassifier.imagehash.ImageHashFunction;
import attatrol.neural.ui.javafx.misc.AbstractUiFactory;
import attatrol.neural.ui.javafx.misc.GenericValueReturnDialog;
import attatrol.neural.ui.javafx.misc.PositiveNumericTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DefaultImageHashFunctionFactory implements AbstractUiFactory<ImageHashFunction> {

    public static final int MINIMAL_QUANT_NUMBER = 10;

    @Override
    public ImageHashFunction generate(Object... parameters) {
        Dialog<ImageHashFunction> dialog = new DefaultImageHashReturnDialog();
        Optional<ImageHashFunction> function = dialog.showAndWait();
        if (function.isPresent()) {
            return function.get();
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Default hash function";
    }

    public static class DefaultImageHashReturnDialog extends GenericValueReturnDialog<ImageHashFunction> {

        private TextField xField = new PositiveNumericTextField();

        private TextField yField = new PositiveNumericTextField();

        public DefaultImageHashReturnDialog() {
            super();
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.add(new Label(String.format(ImageClassifierI18nProvider.getText("defaultimagehashfunctionfactory.labelwidth"),
                    MINIMAL_QUANT_NUMBER)), 0, 1);
            grid.add(xField, 0, 2);
            grid.add(new Label(String.format(ImageClassifierI18nProvider.getText("defaultimagehashfunctionfactory.labelheight"),
                    MINIMAL_QUANT_NUMBER)), 0, 3);
            grid.add(yField, 0, 4);
            this.getDialogPane().setContent(grid);
            this.setTitle(ImageClassifierI18nProvider.getText("defaultimagehashfunctionfactory.title"));
        }

        @Override
        protected ImageHashFunction createResult() {
            return new DefaultImageHashFunction(Integer.parseInt(xField.getText()),
                    Integer.parseInt(yField.getText()));
        }

        @Override
        protected void validate() throws Exception {
            Integer x, y;
            try {
                x = Integer.parseInt(xField.getText());
                y = Integer.parseInt(yField.getText());
                if (x < MINIMAL_QUANT_NUMBER) {
                    throw new IllegalStateException(String.format(ImageClassifierI18nProvider.getText("defaultimagehashfunctionfactory.labelheight"),
                            MINIMAL_QUANT_NUMBER));
                }
                if (y < MINIMAL_QUANT_NUMBER) {
                    throw new IllegalStateException(String.format(ImageClassifierI18nProvider.getText("defaultimagehashfunctionfactory.labelheight"),
                            MINIMAL_QUANT_NUMBER));
                }
            }
            catch (NumberFormatException ex) {
                throw new IllegalStateException(ImageClassifierI18nProvider.getText("defaultimagehashfunctionfactory.errorparse")
                        + ex.getLocalizedMessage());
            }
        }
    }


}
