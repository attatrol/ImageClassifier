package attatrol.neural.ui.javafx.analysis.activation;

import java.util.Optional;

import attatrol.neural.activationfunction.ActivationFunction;
import attatrol.neural.analysis.ActivationFunctionAnalyzer;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.misc.AbstractUiFactory;
import javafx.scene.control.Dialog;

/**
 * Generates some activation function analyzer.
 * @author attatrol
 *
 */
public class ActivationFunctionAnalyzerFactory implements AbstractUiFactory<ActivationFunctionAnalyzer> {

    @Override
    public ActivationFunctionAnalyzer generate(Object... parameters) {
        Dialog<ActivationFunction> dialog = new ActivationFunctionReturnDialog();
        Optional<ActivationFunction> analytical = dialog.showAndWait();
        if (analytical.isPresent()) {
            return new ActivationFunctionAnalyzer(analytical.get());
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return NeuralI18nProvider.getText("activationfunctionanalyzerfactory.name");
    }
}

