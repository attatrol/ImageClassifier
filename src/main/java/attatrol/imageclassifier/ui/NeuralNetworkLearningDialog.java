package attatrol.imageclassifier.ui;

import java.util.Collections;
import java.util.List;

import attatrol.imageclassifier.ImageClassifier.LearningPair;
import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;
import attatrol.neural.NeuralNetworkRuntimeException;
import attatrol.neural.network.NeuralNetwork;
import attatrol.neural.ui.javafx.misc.GenericValueReturnDialog;
import attatrol.neural.ui.javafx.misc.PositiveNumericTextField;
import attatrol.neural.ui.javafx.misc.UiUtils;
import attatrol.neural.utils.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class NeuralNetworkLearningDialog extends GenericValueReturnDialog<Boolean> {

    /*
     * GUI nodes
     */
    private Label statusLabel = new Label();
    {
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    private PositiveNumericTextField iterationNumberTextField = new PositiveNumericTextField();

    private Button startButton = new StartLearningButton();

    private Button stopButton = new StopLearningButton();

    private TableView<IterationInfo> iterationInfo = new IterationInfoTableView();

    /*
     * Internal state
     */
    private final NeuralNetwork neuralNetwork;

    private final List<LearningPair> learningPairs;

    private boolean isLearningProcessAlive;

    private Boolean learningSucceed;

    private ObservableList<IterationInfo> iterationList = FXCollections.observableArrayList();
    {
        iterationInfo.setItems(iterationList);
    }

    public NeuralNetworkLearningDialog(NeuralNetwork neuralNetwork, List<LearningPair> learningPairs) {
        this.neuralNetwork = neuralNetwork;
        this.learningPairs = learningPairs;
        setContent();
    }

    private void setContent() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(statusLabel, 0, 0, 2, 1);
        grid.add(new Label(ImageClassifierI18nProvider.getText(
                "neuralnetworklearningdialog.iterationnumberlabel")), 0, 1);
        grid.add(iterationNumberTextField, 0, 2);
        grid.add(startButton, 0, 3);
        grid.add(stopButton, 0, 4);
        grid.add(iterationInfo, 1, 1, 1, 4);
        getDialogPane().setContent(grid);
        setTitle(ImageClassifierI18nProvider.getText("neuralnetworklearningdialog.title"));
        //override cancel button for stopping learning process on cancel
        final Button cancelButton = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        final EventHandler<ActionEvent> oldEventHandler = cancelButton.getOnAction();
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                isLearningProcessAlive = false;
                if (oldEventHandler != null) {
                    oldEventHandler.handle(event);
                }
            }

        });
    }

    @Override
    protected Boolean createResult() {
        return learningSucceed;
    }

    @Override
    protected void validate() throws Exception {
        if (isLearningProcessAlive) {
            throw new IllegalStateException("Stop learning process first");
        }
    }

    public static class IterationInfo {

        private final int index;

        private final double maxError;

        private final double averageError;

        public IterationInfo(int index, double maxError, double averageError) {
            super();
            this.index = index;
            this.maxError = maxError;
            this.averageError = averageError;
        }

        public int getIndex() {
            return index;
        }

        public double getMaxError() {
            return maxError;
        }

        public double getAverageError() {
            return averageError;
        }
    }

    private static class IterationInfoTableView extends TableView<IterationInfo> {
        @SuppressWarnings("unchecked")
        IterationInfoTableView() {
            super();
            TableColumn<IterationInfo, Integer> indexCol = new TableColumn<>(
                    ImageClassifierI18nProvider.getText("neuralnetworklearningdialog.table.indexcolumn"));
            indexCol.setMinWidth(120);
            indexCol.setCellValueFactory(
                    new PropertyValueFactory<IterationInfo, Integer>("index"));
     
            TableColumn<IterationInfo, Double> maxErrorCol =
                    new TableColumn<>(ImageClassifierI18nProvider.getText("neuralnetworklearningdialog.table.maxerrorcolumn"));
            maxErrorCol.setMinWidth(240);
            maxErrorCol.setCellValueFactory(
                    new PropertyValueFactory<IterationInfo, Double>("maxError"));

            TableColumn<IterationInfo, Double> averageErrorCol =
                    new TableColumn<>(ImageClassifierI18nProvider.getText("neuralnetworklearningdialog.table.avgerrorcolumn"));
            averageErrorCol.setMinWidth(240);
            averageErrorCol.setCellValueFactory(
                    new PropertyValueFactory<IterationInfo, Double>("averageError"));

            this.getColumns().addAll(indexCol, maxErrorCol, averageErrorCol);
        }
    }

    private class StartLearningButton extends Button {

        StartLearningButton() {
            super(ImageClassifierI18nProvider.getText("neuralnetworklearningdialog.startlearningbuttonname"));
            setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    try {
                        int counter = Integer.parseInt(iterationNumberTextField.getText());
                        if (counter > 0) {
                            if (!isLearningProcessAlive) {
                                isLearningProcessAlive = true;
                                iterationList.clear();
                                Thread learnThread = new Thread(new LearnProcess(counter));
                                learnThread.start();
                                learningSucceed = true;
                            }
                            else {
                                UiUtils.showTestMessage(ImageClassifierI18nProvider.getText(
                                        "neuralnetworklearningdialog.concurrentlearningerror"));
                            }
                        }
                        else {
                            UiUtils.showTestMessage(ImageClassifierI18nProvider.getText(
                                    "neuralnetworklearningdialog.zeroiterationserror"));
                        }
                    }
                    catch (NumberFormatException ex) {
                        UiUtils.showTestMessage(ImageClassifierI18nProvider.getText(
                                "neuralnetworklearningdialog.parseerror") + ex.getLocalizedMessage());
                    }
                }

            });
        }
    }

    private class StopLearningButton extends Button {

        StopLearningButton() {
            super(ImageClassifierI18nProvider.getText("neuralnetworklearningdialog.stoplearningbuttonname"));
            setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    isLearningProcessAlive = false;
                }

            });
        }
    }

    private class LearnProcess implements Runnable {

        private final int limit;

        public LearnProcess(int limit) {
            this.limit = limit;
        }

        @Override
        public void run() {
            Platform.runLater(() -> statusLabel.setText(ImageClassifierI18nProvider.getText("neuralnetworklearningdialog.ongoinglearningstatus")));
            int counter = 0;
            final int halfOfSetSize = learningPairs.size() / 2;
            while(counter < limit && isLearningProcessAlive) {
                Collections.shuffle(learningPairs);
                int itemCounter = halfOfSetSize;
                double maxDistance = 0;
                double averageDistance = 0;
                try {
                    for (LearningPair item : learningPairs) {
                        if (itemCounter > 0) {
                            neuralNetwork.learn(item.getInput(), item.getReference());
                            itemCounter--;
                        }
                        else {
                            double[] result = neuralNetwork.map(item.getInput());
                            double difference =
                                    Utils.measureManhattanDistanceBetweenVectors(result, item.getReference());
                            averageDistance += difference;
                            if (difference > maxDistance) {
                                maxDistance = difference;
                            }
                        }
                    }
                }
                catch (NeuralNetworkRuntimeException ex) {
                    isLearningProcessAlive = false;
                    Platform.runLater(() -> UiUtils.showTestMessage(ex.getLocalizedMessage()));
                    learningSucceed = false;
                }
                counter++;
                averageDistance /= halfOfSetSize;
                final IterationInfo info = new IterationInfo(counter, maxDistance, averageDistance);
                iterationList.add(info);
            }
            isLearningProcessAlive = false;
            Platform.runLater(() -> 
                statusLabel.setText(ImageClassifierI18nProvider.getText("neuralnetworklearningdialog.finishedlearningstatus")));
        }

        
    }

}
