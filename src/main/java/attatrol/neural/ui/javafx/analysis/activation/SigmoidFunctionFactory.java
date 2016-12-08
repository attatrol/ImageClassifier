package attatrol.neural.ui.javafx.analysis.activation;

import java.util.Optional;

import attatrol.neural.activationfunction.ActivationFunction;
import attatrol.neural.activationfunction.SigmoidFunction;
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

public class SigmoidFunctionFactory implements AbstractUiFactory<ActivationFunction> {

    @Override
    public ActivationFunction generate(Object... parameters) {
        Dialog<ActivationFunction> dialog = new SigmoidFunctionReturnDialog();
        Optional<ActivationFunction> function = dialog.showAndWait();
        if (function.isPresent()) {
            return function.get();
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return NeuralI18nProvider.getText("sigmoidfunctionfactory.name");
    }

    public static class SigmoidFunctionReturnDialog extends GenericValueReturnDialog<ActivationFunction> {

        private TextField field = new PositiveDoubleTextField();
        {
            field.setText("1");
        }

        public SigmoidFunctionReturnDialog() {
            super();
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.add(new Label(NeuralI18nProvider.getText("sigmoidfunctionfactory.flatnesslabel")), 0, 0);
            grid.add(field, 0, 1);
            this.getDialogPane().setContent(grid);
            this.setTitle(NeuralI18nProvider.getText("sigmoidfunctionfactory.title"));
        }

        @Override
        protected ActivationFunction createResult() {
            return new SigmoidFunction((float) Double.parseDouble(field.getText()));
        }

        @Override
        protected void validate() throws Exception {
            Double result;
            try {
                result = Double.parseDouble(field.getText());
                if (result <= 0.) {
                    throw new IllegalStateException(NeuralI18nProvider
                            .getText("sigmoidfunctionfactory.nonpositiveflatnesserror"));
                }
            }
            catch (NumberFormatException ex) {
                throw new IllegalStateException(
                        NeuralI18nProvider.getText("sigmoidfunctionfactory.parseerror")
                        + ex.getLocalizedMessage());
            }
        }
    }
}
