package attatrol.neural.ui.javafx.learning.supervised.errorminimizer;

import java.util.Optional;

import attatrol.neural.errorfunction.ErrorFunction;
import attatrol.neural.errorfunction.PNorm;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.misc.AbstractUiFactory;
import attatrol.neural.ui.javafx.misc.GenericValueReturnDialog;
import attatrol.neural.ui.javafx.misc.PositiveDoubleTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class PNormFactory implements AbstractUiFactory<ErrorFunction> {

    @Override
    public ErrorFunction generate(Object... parameters) {
        Dialog<ErrorFunction> dialog = new PNormReturnDialog();
        Optional<ErrorFunction> function = dialog.showAndWait();
        if (function.isPresent()) {
            return function.get();
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return NeuralI18nProvider.getText("pnormfactory.name");
    }

    public static class PNormReturnDialog extends GenericValueReturnDialog<ErrorFunction> {

        private TextField field = new PositiveDoubleTextField();

        public PNormReturnDialog() {
            super();
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.add(new Label(NeuralI18nProvider.getText("pnormfactory.pnormlabel")), 0, 0);
            grid.add(field, 0, 1);
            this.getDialogPane().setContent(grid);
            this.setTitle(NeuralI18nProvider.getText("pnormfactory.title"));
        }

        @Override
        protected ErrorFunction createResult() {
            return new PNorm(Double.parseDouble(field.getText()));
        }

        @Override
        protected void validate() throws Exception {
            Double result;
            try {
                result = Double.parseDouble(field.getText());
                if (result <= 1.) {
                    throw new IllegalStateException(NeuralI18nProvider.getText("pnormfactory.badperror"));
                }
            }
            catch (NumberFormatException ex) {
                throw new IllegalStateException(NeuralI18nProvider.getText("pnormfactory.parseerror")
                        + ex.getLocalizedMessage());
            }
        }
    }
}
