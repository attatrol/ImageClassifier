package attatrol.imageclassifier.ui;

import java.util.Optional;

import attatrol.imageclassifier.ImageClass;
import attatrol.imageclassifier.ImageClassifier;
import attatrol.imageclassifier.ImageClassifierException;
import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;
import attatrol.imageclassifier.imagehash.ImageHashFunction;
import attatrol.imageclassifier.ui.imagehash.DefaultImageHashFunctionFactory;
import attatrol.neural.network.NeuralNetwork;
import attatrol.neural.ui.javafx.NeuralNetworkFactoryDialog;
import attatrol.neural.ui.javafx.misc.FactoryComboBox;
import attatrol.neural.ui.javafx.misc.UiUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("unchecked")
public class MainForm extends Scene {

    /*
     * GUI nodes
     */

    private Label statusLabel = new Label();
    {
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    private FactoryComboBox<ImageHashFunction> hashFunctioncomboBox = new FactoryComboBox<>();
    {
        final EventHandler<Event> oldEvent = hashFunctioncomboBox.getOnHidden();
        hashFunctioncomboBox.setOnHidden(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                oldEvent.handle(event);
                if (hashFunctioncomboBox.getResult() != null) {
                    setState(InternalState.HASH_CHOSEN);
                    hashFunction = hashFunctioncomboBox.getResult();
                } else {
                    setState(InternalState.INITIAL_STATE);
                }
            }

        });
        hashFunctioncomboBox.getItems().addAll(new DefaultImageHashFunctionFactory());
    }

    private Button classSetupButton = new ClassSetupButton();

    private Button networkSetupButton = new NeuralSetupButton();

    private Button teachButton = new TeachButton();

    private Button useButton = new UseButton();

    /**
     * Holds state of the table view.
     */
    private ObservableList<ImageClass> tableRows = FXCollections.observableArrayList();

    /*
     * Internal state
     */
    @SuppressWarnings("unused") // may be used in future
    private InternalState state;

    private ImageHashFunction hashFunction;

    private ImageClassifier classifier;

    private NeuralNetwork neuralNetwork;

    /**
     * Populates scene with all of GUI elements.
     * @return complete scene ready to be shown
     */
    public MainForm() {
        super(new BorderPane());
        // menu bar
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu(ImageClassifierI18nProvider.getText("mainform.menutitle"));
        MenuItem saveMenuOption = new MenuItem(ImageClassifierI18nProvider.getText("mainform.savemenuitem"));
        MenuItem loadMenuOption = new MenuItem(ImageClassifierI18nProvider.getText("mainform.loadmenuitem"));
        menuFile.getItems().addAll(saveMenuOption, loadMenuOption);
        menuBar.getMenus().addAll(menuFile);
        // grid pane
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(statusLabel, 0, 0, 2, 1);
        grid.add(new Label(ImageClassifierI18nProvider.getText("mainform.label1")), 0, 2);
        grid.add(hashFunctioncomboBox, 0, 3);
        grid.add(new Label(ImageClassifierI18nProvider.getText("mainform.label2")), 0, 4);
        grid.add(classSetupButton, 0, 5);
        grid.add(new Label(ImageClassifierI18nProvider.getText("mainform.label3")), 0, 6);
        grid.add(networkSetupButton, 0, 7);
        grid.add(new Label(ImageClassifierI18nProvider.getText("mainform.label4")), 0, 8);
        grid.add(teachButton, 0, 9);
        grid.add(new Label(ImageClassifierI18nProvider.getText("mainform.label5")), 0, 10);
        grid.add(useButton, 0, 11);
        TableView<ImageClass> tableView = new ClassifierPresentationTable();
        tableView.setItems(tableRows);
        grid.add(tableView, 1, 1, 1, 13);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
        GridPane.setVgrow(tableView, Priority.ALWAYS);
        tableView.setMinWidth(1000);

        // border pane
        BorderPane root = (BorderPane) getRoot();
        root.setTop(menuBar);
        root.setCenter(grid);
        menuBar.prefWidthProperty().bind(root.widthProperty());
        this.setRoot(root);

        setState(InternalState.INITIAL_STATE);

    }

    private void setState(InternalState newState) {
        state = newState;
        statusLabel.setText(newState.getStatusString());
        switch (newState) {
            case INITIAL_STATE:
                classSetupButton.setDisable(true);
                networkSetupButton.setDisable(true);
                teachButton.setDisable(true);
                useButton.setDisable(true);
                tableRows.clear();
                break;
            case HASH_CHOSEN:
                classSetupButton.setDisable(false);
                networkSetupButton.setDisable(true);
                teachButton.setDisable(true);
                useButton.setDisable(true);
                tableRows.clear();
                break;
            case CLASSIFIER_SET:
                classSetupButton.setDisable(false);
                networkSetupButton.setDisable(false);
                teachButton.setDisable(true);
                useButton.setDisable(true);
                break;
            case NETWORK_SET:
                classSetupButton.setDisable(false);
                networkSetupButton.setDisable(false);
                teachButton.setDisable(false);
                useButton.setDisable(true);
                break;
            case NETWORK_LEARNED:
                classSetupButton.setDisable(false);
                networkSetupButton.setDisable(false);
                teachButton.setDisable(false);
                useButton.setDisable(false);
                break;
        }
    }

    private class ClassSetupButton extends Button {

        ClassSetupButton() {
            super(ImageClassifierI18nProvider.getText("mainform.setimageclassesbuttonname"));
            this.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ClassifierSetupDialog setupForm = new ClassifierSetupDialog(
                            (Stage) MainForm.this.getWindow(), new ImageClassifier(classifier),
                            hashFunction);
                    final Optional<ImageClassifier> imageClassifier = setupForm.showAndWait();
                    if (imageClassifier.isPresent()) {
                        tableRows.clear();
                        classifier = imageClassifier.get();
                        tableRows.addAll(classifier.getClassesAsRows());
                        setState(InternalState.CLASSIFIER_SET);
                    }
                }
            });
        }
    }

    private class NeuralSetupButton extends Button {

        NeuralSetupButton() {
            super(ImageClassifierI18nProvider.getText("mainform.setupneuralbuttonname"));
            this.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (classifier.getSize() > 1) {
                        NeuralNetworkFactoryDialog setupForm = new NeuralNetworkFactoryDialog(
                                hashFunction.getResultSize(), classifier.getSize(), 1.);
                        final Optional<NeuralNetwork> network = setupForm.showAndWait();
                        if (network.isPresent()) {
                            neuralNetwork = network.get();
                            setState(InternalState.NETWORK_SET);
                        }
                    } else {
                        UiUtils.showTestMessage(
                                ImageClassifierI18nProvider.getText("mainform.lessthan2classeserror"));
                    }
                }
            });
        }
    }

    private class TeachButton extends Button {
        TeachButton() {
            super(ImageClassifierI18nProvider.getText("mainform.teachbuttonname"));
            this.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    NeuralNetworkLearningDialog teachForm;
                    try {
                        teachForm = new NeuralNetworkLearningDialog(neuralNetwork,
                                classifier.getLearningPairs());
                        final Optional<Boolean> teachingStatus = teachForm.showAndWait();
                        if (teachingStatus.isPresent()) {
                            if (teachingStatus.get()) {
                                setState(InternalState.NETWORK_LEARNED);
                            } else {
                                UiUtils.showTestMessage(ImageClassifierI18nProvider
                                        .getText("mainform.networklearningfailederror"));
                            }
                        }

                    } catch (ImageClassifierException ex) {
                        UiUtils.showTestMessage(ex.getLocalizedMessage());
                    }
                }
            });
        }
    }

    private class UseButton extends Button {
        UseButton() {
            super(ImageClassifierI18nProvider.getText("mainform.usebuttonname"));
            this.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    AnalyzeImageForm analyzeForm = new AnalyzeImageForm(neuralNetwork, classifier, hashFunction);
                    //analyzeForm.initOwner((Window) MainForm.this);
                    analyzeForm.initModality(Modality.APPLICATION_MODAL);
                    analyzeForm.show();
                }

            });
        }

    }

    private enum InternalState {
        INITIAL_STATE(ImageClassifierI18nProvider.getText("mainform.initialstatus")),
        HASH_CHOSEN(ImageClassifierI18nProvider.getText("mainform.hashchosenstatus")),
        CLASSIFIER_SET(ImageClassifierI18nProvider.getText("mainform.classifiersetstatus")),
        NETWORK_SET(ImageClassifierI18nProvider.getText("mainform.networksetstatus")),
        NETWORK_LEARNED(ImageClassifierI18nProvider.getText("mainform.networklearnedstatus"));

        private String statusString;

        InternalState(String statusString) {
            this.statusString = statusString;
        }

        public String getStatusString() {
            return statusString;
        }
    }
}
