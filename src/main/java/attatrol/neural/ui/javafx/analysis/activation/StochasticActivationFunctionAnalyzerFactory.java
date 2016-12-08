package attatrol.neural.ui.javafx.analysis.activation;

import java.util.Optional;

import attatrol.neural.activationfunction.ActivationFunction;
import attatrol.neural.analysis.StochasticActivationFunctionAnalyzer;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.misc.AbstractUiFactory;
import javafx.scene.control.Dialog;

/**
 * Generates some activation function analyzer.
 * @author attatrol
 *
 */
public class StochasticActivationFunctionAnalyzerFactory implements AbstractUiFactory<StochasticActivationFunctionAnalyzer> {

    @Override
    public StochasticActivationFunctionAnalyzer generate(Object... parameters) {
        Dialog<ActivationFunction> dialog = new ActivationFunctionReturnDialog();
        Optional<ActivationFunction> analytical = dialog.showAndWait();
        if (analytical.isPresent()) {
            return new StochasticActivationFunctionAnalyzer(analytical.get());
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return NeuralI18nProvider
                .getText("stochasticactivationfunctionanalyzerfactory.name");
    }
}

