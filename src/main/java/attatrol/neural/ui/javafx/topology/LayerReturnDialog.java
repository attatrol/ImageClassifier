package attatrol.neural.ui.javafx.topology;

import attatrol.neural.topology.Layer;
import attatrol.neural.topology.LayerInterconnectionDistribution;
import attatrol.neural.topology.LayerType;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.misc.GenericValueReturnDialog;
import attatrol.neural.ui.javafx.misc.PositiveNumericTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LayerReturnDialog extends GenericValueReturnDialog<Layer> {

    private TextField layerSizeTextField = new PositiveNumericTextField();

    private TextField numberOfChildNeuronsTextField = new PositiveNumericTextField();

    private ComboBox<LayerInterconnectionDistribution> lidComboBox = new ComboBox<>();
    {
        lidComboBox.getItems().addAll(LayerInterconnectionDistribution.values());
        lidComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                lidInfo.setText(lidComboBox.getValue().getDescription());
            }
            
        });
    }

    private TextArea lidInfo = new TextArea();
    {
        lidInfo.setWrapText(true);
        lidInfo.setEditable(false);
        lidInfo.setMaxHeight(100);
    }

    private final LayerType layerType;

    public LayerReturnDialog(boolean firstLayer, int firstLayerSize) {
        layerType = firstLayer ? LayerType.INPUT_VECTOR : LayerType.ORDINARY;
        if (firstLayer) {
            layerSizeTextField.setText(Integer.toString(firstLayerSize));
            layerSizeTextField.setDisable(true);
        }
        setContent();
    }

    private void setContent() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(new Label(NeuralI18nProvider.getText("layerreturndialog.layersizelabel")), 0, 0);
        grid.add(layerSizeTextField, 0, 1);
        grid.add(new Label(NeuralI18nProvider.getText("layerreturndialog.childcountlabel")), 0, 2);
        grid.add(numberOfChildNeuronsTextField, 0, 3);
        grid.add(new Label(NeuralI18nProvider.getText("layerreturndialog.distributionlabel")), 0, 4);
        grid.add(lidComboBox, 0, 5);
        grid.add(lidInfo, 0, 6);
        this.getDialogPane().setContent(grid);
        this.setTitle(NeuralI18nProvider.getText("layerreturndialog.title"));
    }

    @Override
    protected Layer createResult() {
        return new Layer(Integer.parseInt(layerSizeTextField.getText()),
                Integer.parseInt(numberOfChildNeuronsTextField.getText()),
                lidComboBox.getValue(), layerType);
    }

    @Override
    protected void validate() throws Exception {
        if (lidComboBox.getValue() == null) {
            throw new IllegalStateException(NeuralI18nProvider.getText("layerreturndialog.nulldistributionerror"));
        }
        try {
            Integer.parseInt(layerSizeTextField.getText());
            Integer.parseInt(numberOfChildNeuronsTextField.getText());
        }
        catch (NumberFormatException ex) {
            throw new IllegalStateException(NeuralI18nProvider.getText("layerreturndialog.parseerror")
                    + ex.getLocalizedMessage());
        }
    }

}
