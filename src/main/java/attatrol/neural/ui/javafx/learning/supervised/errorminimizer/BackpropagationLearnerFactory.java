package attatrol.neural.ui.javafx.learning.supervised.errorminimizer;

import java.util.Optional;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.errorfunction.ErrorFunction;
import attatrol.neural.learning.LearningProcessor;
import attatrol.neural.learning.supervised.BackpropagationLearner;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.misc.AbstractUiFactory;
import attatrol.neural.ui.javafx.misc.PositiveDoubleReturnDialog;
import javafx.scene.control.Dialog;

public class BackpropagationLearnerFactory implements AbstractUiFactory<LearningProcessor> {

    @Override
    public LearningProcessor generate(Object... parameters) {
        Dialog<ErrorFunction> errorFunctionDialog = new ErrorFunctionReturnDialog();
        Optional<ErrorFunction> errorFunction = errorFunctionDialog.showAndWait();
        Dialog<Double> changeFactorDialog = new PositiveDoubleReturnDialog(NeuralI18nProvider
                .getText("backpropagationlearnerfactory.dialog.title"),
                NeuralI18nProvider.getText("backpropagationlearnerfactory.dialog.label"));
        Optional<Double> changeFactor = changeFactorDialog.showAndWait();
        if (errorFunction.isPresent() && changeFactor.isPresent()) {
            try {
                return BackpropagationLearner.getBackpropagationLearner(errorFunction.get(), changeFactor.get().floatValue());
            } catch (NeuralNetworkGenerationException e) {
                return null; // will never happen
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return NeuralI18nProvider.getText("backpropagationlearnerfactory.name");
    }
}
