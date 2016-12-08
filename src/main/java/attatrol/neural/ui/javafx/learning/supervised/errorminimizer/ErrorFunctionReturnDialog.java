package attatrol.neural.ui.javafx.learning.supervised.errorminimizer;

import attatrol.neural.errorfunction.ErrorFunction;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.misc.FactoryComboBox;
import attatrol.neural.ui.javafx.misc.GenericValueReturnDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

@SuppressWarnings("unchecked")
public class ErrorFunctionReturnDialog extends GenericValueReturnDialog<ErrorFunction> {

    private FactoryComboBox<ErrorFunction> comboBox = new FactoryComboBox<>();
    {
        //XXX here we fill checklist with new functions
        comboBox.getItems().addAll(
                new PNormFactory(),
                new QuadraticErrorFactory());
    }

    public ErrorFunctionReturnDialog() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(new Label(NeuralI18nProvider.getText("errorfunctionreturndialog.chooseerrorfunclabel")), 0, 0);
        grid.add(comboBox, 0, 1);
        this.getDialogPane().setContent(grid);
        this.setTitle(NeuralI18nProvider.getText("errorfunctionreturndialog.title"));
    }

    @Override
    protected ErrorFunction createResult() {
        return comboBox.getResult();
    }

    @Override
    protected void validate() throws Exception {
        if (comboBox.getResult() == null) {
            throw new IllegalStateException(NeuralI18nProvider
                    .getText("errorfunctionreturndialog.nullfunctionerror"));
        }
    }

}

