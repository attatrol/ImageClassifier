package attatrol.neural.ui.javafx.learning.supervised.errorminimizer;

import attatrol.neural.errorfunction.ErrorFunction;
import attatrol.neural.errorfunction.QuadraticError;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.misc.AbstractUiFactory;

public class QuadraticErrorFactory implements AbstractUiFactory<ErrorFunction> {

    @Override
    public ErrorFunction generate(Object... parameters) {
        return new QuadraticError();
    }

    @Override
    public String toString() {
        return NeuralI18nProvider.getText("quadraticerrorfactory.name");
    }

}
