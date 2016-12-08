package attatrol.neural.ui.javafx.topology;

import java.util.Optional;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.topology.Layer;
import attatrol.neural.topology.LayerInterconnectionDistribution;
import attatrol.neural.topology.LayerType;
import attatrol.neural.topology.LayeredTopologyDescription;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.misc.GenericValueReturnDialog;
import attatrol.neural.ui.javafx.misc.UiUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class LayeredTopologyDescriptionReturnDialog extends GenericValueReturnDialog<LayeredTopologyDescription> {

    private final int inputVectorSize;

    @SuppressWarnings("unused")
    private final int resultVectorSize;

    private Layer finalLayer;

    private LayeredTopologyDescription result;

    private ObservableList<Layer> layers = FXCollections.observableArrayList();

    private ListView<Layer> listView = new ListView<>();
    {
        listView.setItems(layers);
    }

    private Button addLayerButton = new AddLayerButton();

    private Button removeLayerButton = new RemoveLayerButton();

    public LayeredTopologyDescriptionReturnDialog(int inputVectorSize, int resultVectorSize) {
        super();
        this.inputVectorSize = inputVectorSize;
        this.resultVectorSize = resultVectorSize;
        finalLayer = new Layer(resultVectorSize, 1, LayerInterconnectionDistribution.LOCALIZED, LayerType.SURFACE);
        setDialogContent();
    }

    @Override
    protected LayeredTopologyDescription createResult() {
        return result;
    }

    @Override
    protected void validate() throws Exception {
        result = buildDescription();
    }

    private void setDialogContent() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(addLayerButton, 0, 0);
        grid.add(removeLayerButton, 0, 1);
        grid.add(listView, 1, 0, 1, 7);
        GridPane.setHgrow(listView, Priority.ALWAYS);
        GridPane.setVgrow(listView, Priority.ALWAYS);
        listView.setMinWidth(1000);
        Label surfaceLayerLabel = new Label(finalLayer.toString());
        surfaceLayerLabel.setStyle("-fx-border-color:grey; -fx-background-color: white;");
        grid.add(surfaceLayerLabel, 1, 8);
        surfaceLayerLabel.setMinWidth(1000);
        this.getDialogPane().setContent(grid);
        this.setTitle(NeuralI18nProvider.getText("ltdreturndialog.title"));
    }

    private LayeredTopologyDescription buildDescription() throws NeuralNetworkGenerationException {
        LayeredTopologyDescription description = new LayeredTopologyDescription();
        for (Layer layer : layers) {
            description.addLayer(layer);
        }
        description.addLayer(finalLayer);
        return description;
    }

    private LayeredTopologyDescription buildPartialDescription(Layer newLayer) throws NeuralNetworkGenerationException {
        LayeredTopologyDescription description = new LayeredTopologyDescription();
        for (Layer layer : layers) {
            description.addLayer(layer);
        }
        description.addLayer(newLayer);
        return description;
    }

    private class AddLayerButton extends Button {

        public AddLayerButton() {
            setText(NeuralI18nProvider.getText("ltdreturndialog.addlayerbuttonname"));
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Dialog<Layer> dialog = new LayerReturnDialog(layers.isEmpty(), inputVectorSize);
                    Optional<Layer> layer = dialog.showAndWait();
                    if (layer.isPresent()) {
                        try {
                            buildPartialDescription(layer.get());
                            layers.add(layer.get());
                        }
                        catch(NeuralNetworkGenerationException ex) {
                            UiUtils.showTestMessage(ex.getLocalizedMessage());
                        }
                    }
                }
            });
        }
    }

    private class RemoveLayerButton extends Button {

        public RemoveLayerButton() {
            setText(NeuralI18nProvider.getText("ltdreturndialog.removelayerbuttonname"));
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!layers.isEmpty()) {
                        layers.remove(layers.size() - 1);
                    }
                }
            });
        }
    }

}
