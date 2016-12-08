package attatrol.neural.ui.javafx;

import java.util.Optional;

import attatrol.neural.analysis.AnalyticalProcessor;
import attatrol.neural.learning.LearningProcessor;
import attatrol.neural.network.NeuralNetwork;
import attatrol.neural.network.NeuralNetworkFactory;
import attatrol.neural.network.NeuralNetworkSettings;
import attatrol.neural.network.TopologySetting;
import attatrol.neural.topology.Layer;
import attatrol.neural.topology.LayeredTopologyDescription;
import attatrol.neural.ui.javafx.analysis.activation.ActivationFunctionAnalyzerFactory;
import attatrol.neural.ui.javafx.analysis.activation.StochasticActivationFunctionAnalyzerFactory;
import attatrol.neural.ui.javafx.i18n.NeuralI18nProvider;
import attatrol.neural.ui.javafx.learning.supervised.errorminimizer.BackpropagationLearnerFactory;
import attatrol.neural.ui.javafx.misc.FactoryComboBox;
import attatrol.neural.ui.javafx.misc.GenericValueReturnDialog;
import attatrol.neural.ui.javafx.topology.LayeredTopologyDescriptionReturnDialog;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Ui for {@link attatrol.neural.network.NeuralNetworkFactory}. Modal window
 * with instruments for easy creation of
 * {@link attatrol.neural.network.NeuralNetwork} instances. Users should
 * subscribe for {@link NeuralNetworkFactoryEvent} for getting their instance of
 * the generated neural network.
 * @author attatrol
 */
@SuppressWarnings("unchecked")
public class NeuralNetworkFactoryDialog extends GenericValueReturnDialog<NeuralNetwork> {

    private final int inputVectorSize;

    private final int resultVectorSize;

    private double vectorAmplitude;

    private TextArea analyticalProcessorInfo = new TextArea();
    {
        analyticalProcessorInfo.setWrapText(true);
        analyticalProcessorInfo.setEditable(false);
        analyticalProcessorInfo.setMaxHeight(100);
    }

    private TextArea learningProcessorInfo = new TextArea();
    {
        learningProcessorInfo.setWrapText(true);
        learningProcessorInfo.setEditable(false);
        learningProcessorInfo.setMaxHeight(100);
    }

    private TextArea topologySettingInfo = new TextArea();
    {
        topologySettingInfo.setWrapText(true);
        topologySettingInfo.setEditable(false);
        topologySettingInfo.setMaxHeight(100);
    }

    private FactoryComboBox<AnalyticalProcessor> analyticalProcessorComboBox = new FactoryComboBox<>();
    {
        final EventHandler<Event> oldEvent = analyticalProcessorComboBox.getOnHidden();
        analyticalProcessorComboBox.setOnHidden(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                oldEvent.handle(event);
                analyticalProcessorInfo.clear();
                if (analyticalProcessorComboBox.getResult() != null) {
                    analyticalProcessorInfo
                            .setText(analyticalProcessorComboBox.getResult().toString());
                }
            }

        });
        // XXX here to populate combo box with any analytical processor
        // factories
        analyticalProcessorComboBox.getItems().addAll(new ActivationFunctionAnalyzerFactory(),
                new StochasticActivationFunctionAnalyzerFactory());
    }

    private FactoryComboBox<LearningProcessor> learningProcessorComboBox = new FactoryComboBox<>();
    {
        final EventHandler<Event> oldEvent = learningProcessorComboBox.getOnHidden();
        learningProcessorComboBox.setOnHidden(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                oldEvent.handle(event);
                learningProcessorInfo.clear();
                if (learningProcessorComboBox.getResult() != null) {
                    learningProcessorInfo
                            .setText(learningProcessorComboBox.getResult().toString());
                }
            }

        });
        // XXX here to populate combo box with any analytical processor
        // factories
        learningProcessorComboBox.getItems().addAll(new BackpropagationLearnerFactory());
    }

    private LayeredTopologyDescription ltd;

    private ComboBox<TopologySetting> topologySettingCombobox = new ComboBox<>();
    {
        topologySettingCombobox.getItems().addAll(TopologySetting.USER_OPTIONS);
        topologySettingCombobox.getItems().add(TopologySetting.PRESET_TOPOLOGY_DESCRIPTION);
        topologySettingCombobox.setOnHidden(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                if (topologySettingCombobox
                        .getValue() == TopologySetting.PRESET_TOPOLOGY_DESCRIPTION) {
                    Dialog<LayeredTopologyDescription> ltdDialog = new LayeredTopologyDescriptionReturnDialog(
                            inputVectorSize, resultVectorSize);
                    Optional<LayeredTopologyDescription> ltdOption = ltdDialog.showAndWait();
                    if (ltdOption.isPresent()) {
                        ltd = ltdOption.get();
                        layers.setDisable(false);
                        layers.getItems().clear();
                        layers.getItems().addAll(ltd.getLayers());
                        topologySettingInfo.setText(topologySettingCombobox
                        .getValue().getDescription());
                    } else if (ltd == null) {
                        topologySettingCombobox.getSelectionModel().clearSelection();
                        topologySettingInfo.clear();
                    }
                } else if (topologySettingCombobox
                        .getValue() != null) {
                    layers.setDisable(true);
                    topologySettingInfo.setText(topologySettingCombobox
                            .getValue().getDescription());
                }
            }

        });
    }

    ListView<Layer> layers = new ListView<>();
    {
        layers.setDisable(true);
    }

    private NeuralNetwork result;

    public NeuralNetworkFactoryDialog(int inputVectorSize, int resultVectorSize,
            double vectorAmplitude) {
        super();
        this.inputVectorSize = inputVectorSize;
        this.resultVectorSize = resultVectorSize;
        this.vectorAmplitude = vectorAmplitude;
        setDialogContent();
    }

    private void setDialogContent() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(new Label(NeuralI18nProvider.getText("neuralnetworkfactorydialog.setupanalyticallabel")), 0, 0);
        grid.add(analyticalProcessorComboBox, 0, 1);
        grid.add(analyticalProcessorInfo, 0, 2);
        grid.add(new Label(NeuralI18nProvider.getText("neuralnetworkfactorydialog.setuplearninglabel")), 0, 3);
        grid.add(learningProcessorComboBox, 0, 4);
        grid.add(learningProcessorInfo, 0, 5);
        grid.add(new Label(NeuralI18nProvider.getText("neuralnetworkfactorydialog.setuptopologylabel")), 0, 6);
        grid.add(topologySettingCombobox, 0, 7);
        grid.add(topologySettingInfo, 0, 8);
        grid.add(layers, 1, 0, 1, 9);
        GridPane.setHgrow(layers, Priority.ALWAYS);
        GridPane.setVgrow(layers, Priority.ALWAYS);
        layers.setMinWidth(1000);
        this.getDialogPane().setContent(grid);
        this.setTitle(NeuralI18nProvider.getText("neuralnetworkfactorydialog.title"));
    }

    @Override
    public NeuralNetwork createResult() {
        return result;
    }

    @Override
    protected void validate() throws Exception {
        final TopologySetting topologySetting = topologySettingCombobox.getValue();
        final NeuralNetworkSettings settings;
        if (topologySetting == TopologySetting.PRESET_TOPOLOGY_DESCRIPTION) {
            settings = new NeuralNetworkSettings(vectorAmplitude, ltd,
                    analyticalProcessorComboBox.getResult(), learningProcessorComboBox.getResult());
        } else {
            settings = new NeuralNetworkSettings(inputVectorSize, resultVectorSize, vectorAmplitude,
                    topologySetting, analyticalProcessorComboBox.getResult(),
                    learningProcessorComboBox.getResult());
        }
        result = NeuralNetworkFactory.getNetwork(settings);
    }

}
