package attatrol.imageclassifier.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import attatrol.imageclassifier.ImageClassifier;
import attatrol.imageclassifier.ImageClassifierException;
import attatrol.imageclassifier.imagehash.ImageHashFunction;
import attatrol.neural.NeuralNetworkRuntimeException;
import attatrol.neural.network.NeuralNetwork;
import attatrol.neural.ui.javafx.misc.UiUtils;
import attatrol.neural.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AnalyzeImageForm extends Stage {

    public static final double HIGHLIGHT_THRESHOLD = 0.5;

    public static final ExtensionFilter IMAGE_EXTENTION_FILTER ;
    static { 
        List<String> extentions = new ArrayList<>();
        for (String token : ImageIO.getReaderFileSuffixes()) {
            extentions.add("*." + token);
        }
        IMAGE_EXTENTION_FILTER = new ExtensionFilter(
            "Supported images", extentions);
    }

    private static File oldFileDirectory;

    /*
     * GUI objects
     */
    private Button chooseFileButton = new ChooseFileButton();

    private TableView<ClassificationResult> resultView = new ClassificationResultTableView();

    private Label fileNameLabel = new Label("File not chosen");

    private ImageView imageView = new ImageView();
    {
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
    }

    /*
     * Internal state
     */
    private final NeuralNetwork network;

    private final ImageClassifier classifier;

    private final ImageHashFunction hashFunction;

    public AnalyzeImageForm(NeuralNetwork network, ImageClassifier classifier,
            ImageHashFunction hashFunction) {
        this.network = network;
        this.classifier = classifier;
        this.hashFunction = hashFunction;
        // grid setup
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(fileNameLabel, 0, 0, 2, 1);
        grid.add(chooseFileButton, 0, 1);
        grid.add(imageView, 0, 2);
        grid.add(resultView, 1, 1, 1, 4);
        this.setScene(new Scene(grid));
    }

    private List<ClassificationResult> analyzeImage(File imageFile)
            throws NeuralNetworkRuntimeException, ImageClassifierException {
        double[] resultVector = network.map(hashFunction.hash(imageFile));
        int maxIndex = Utils.getIndexOfMaxElement(resultVector);
        List<ClassificationResult> result = new ArrayList<>(resultVector.length);
        String[] classNames = classifier.getClassNames();
        for (int i = 0; i < resultVector.length; i++) {
            final ClassificationResultThreshold threshold;
            if (i == maxIndex) {
                threshold = ClassificationResultThreshold.MAXIMAL;
            } else if (resultVector[i] >= HIGHLIGHT_THRESHOLD) {
                threshold = ClassificationResultThreshold.UPPER_HALF;
            } else {
                threshold = ClassificationResultThreshold.LOWER_HALF;
            }
            result.add(new ClassificationResult(classNames[i], resultVector[i], threshold));
        }
        return result;
    }

    private void showImage(File imageFile) throws FileNotFoundException {
        final Image image = new Image(new FileInputStream(imageFile));
        imageView.setImage(image);
    }

    public class ChooseFileButton extends Button {

        private FileChooser fileChooser = new FileChooser();
        {
            fileChooser.setTitle("Choose your image file:");
            if (oldFileDirectory != null) {
                fileChooser.setInitialDirectory(oldFileDirectory);
            }
            fileChooser.getExtensionFilters().add(IMAGE_EXTENTION_FILTER);
            fileChooser.setSelectedExtensionFilter(IMAGE_EXTENTION_FILTER);
        }

        public ChooseFileButton() {
            super("Choose file button");
            setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    final File imageFile = fileChooser.showOpenDialog(null);
                    if (imageFile != null) {
                        try {
                            final File newDirectory = imageFile.getParentFile();
                            fileChooser.setInitialDirectory(newDirectory);
                            oldFileDirectory = newDirectory;
                            showImage(imageFile);
                            fileNameLabel.setText(imageFile.toString());
                            resultView.getItems().clear();
                            try {
                                List<ClassificationResult> result = analyzeImage(imageFile);
                                resultView.getItems().addAll(result);
                            } catch (NeuralNetworkRuntimeException | ImageClassifierException ex) {
                                UiUtils.showTestMessage(ex.getLocalizedMessage());
                            }
                        } catch (FileNotFoundException ex1) {
                            UiUtils.showTestMessage("Failed to find a file");
                        }
                    }
                }

            });
        }
    }

    public enum ClassificationResultThreshold {
        LOWER_HALF(null),
        UPPER_HALF("-fx-background-color: palegreen;"),
        MAXIMAL("-fx-background-color: skyblue;");

        private String style;

        ClassificationResultThreshold(String style) {
            this.style = style;
        }

        public String getStyle() {
            return style;
        }
    }

    public static class ClassificationResult {

        private final String className;

        private final double classAffinity;

        private final ClassificationResultThreshold threshold;

        public ClassificationResult(String className, double classAffinity,
                ClassificationResultThreshold threshold) {
            this.className = className;
            this.classAffinity = classAffinity;
            this.threshold = threshold;
        }

        public String getClassName() {
            return className;
        }

        public double getClassAffinity() {
            return classAffinity;
        }

        public ClassificationResultThreshold getThreshold() {
            return threshold;
        }
    }

    private static class ClassificationResultTableView extends TableView<ClassificationResult> {

        @SuppressWarnings("unchecked")
        public ClassificationResultTableView() {
            super();
            TableColumn<ClassificationResult, String> className = new TableColumn<>("Class name");
            className.setMinWidth(430);
            className.setCellValueFactory(
                    new PropertyValueFactory<ClassificationResult, String>("className"));

            TableColumn<ClassificationResult, Double> classAffinity = new TableColumn<>(
                    "Class affinity");
            classAffinity.setMinWidth(230);
            classAffinity.setCellValueFactory(
                    new PropertyValueFactory<ClassificationResult, Double>("classAffinity"));

            setRowFactory(
                    new Callback<TableView<ClassificationResult>, TableRow<ClassificationResult>>() {
                        @Override
                        public TableRow<ClassificationResult> call(
                                TableView<ClassificationResult> tableView) {
                            final TableRow<ClassificationResult> row = new TableRow<ClassificationResult>() {
                                @Override
                                protected void updateItem(ClassificationResult result,
                                        boolean empty) {
                                    super.updateItem(result, empty);
                                    if (!empty) {
                                        setStyle(result.getThreshold().getStyle());
                                    } else {
                                        setStyle(null);
                                    }
                                }
                            };
                            return row;
                        }
                    });

            this.getColumns().addAll(className, classAffinity);
        }
    }
}
