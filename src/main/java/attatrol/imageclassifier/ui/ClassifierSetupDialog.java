package attatrol.imageclassifier.ui;

import java.util.Optional;

import attatrol.imageclassifier.ImageClass;
import attatrol.imageclassifier.ImageClassifier;
import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;
import attatrol.imageclassifier.imagehash.ImageHashFunction;
import attatrol.neural.ui.javafx.misc.UiUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ClassifierSetupDialog extends Dialog<ImageClassifier> {

    private ImageClassifier imageClassifier;

    private ImageHashFunction hashFunction;

    private ObservableList<ImageClass> tableViewClassList = FXCollections.observableArrayList();

    private TableView<ImageClass> table = new ClassifierPresentationTable();

    private Button addClassButton;

    private Button deleteClassButton;

    public ClassifierSetupDialog(Stage rootStage, ImageClassifier classifier, ImageHashFunction hashFunction) {
        super();
        this.hashFunction = hashFunction;
        imageClassifier = classifier == null ? new ImageClassifier() : classifier;
        tableViewClassList.addAll(imageClassifier.getClassesAsRows());
        setupScene(rootStage);
    }

    private void setupScene(Stage rootStage) {
        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        addClassButton = new AddClassButton();
        deleteClassButton = new DeleteClassButton();
        table.setItems(tableViewClassList);
        // grid setup
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(new Label(String.format(ImageClassifierI18nProvider.getText("classifiersetupdialog.note"),
                ImageClassifier.MINIMAL_CLASS_SIZE)), 0, 0, 2, 1);
        grid.add(addClassButton, 0, 1);
        grid.add(deleteClassButton, 0, 2);
        grid.add(table, 1, 1, 1, 6);
        this.getDialogPane().setContent(grid);
        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return imageClassifier;
            }
            return null;
        });
    }

    private class AddClassButton extends Button {

        AddClassButton() {
            super(ImageClassifierI18nProvider.getText("classifiersetupdialog.addclassbuttonname"));
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Dialog<ImageClass> dialog = new ImageClassReturnDialog(hashFunction);
                    Optional<ImageClass> iClass = dialog.showAndWait();
                    if (iClass.isPresent()) {
                        try {
                            imageClassifier.addImageClass(iClass.get());
                            tableViewClassList.add(iClass.get());
                        }
                        catch (IllegalArgumentException ex) {
                            UiUtils.showTestMessage(ex.getLocalizedMessage());
                        }
                    }
                }
            });
        }
    }

    private class DeleteClassButton extends Button {

        DeleteClassButton() {
            super(ImageClassifierI18nProvider.getText("classifiersetupdialog.deleteclassbuttonname"));
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    final ImageClass imageClass = table.getSelectionModel().getSelectedItem();
                    if (imageClass != null) {
                        imageClassifier.removeImageClass(imageClass);
                        tableViewClassList.remove(imageClass);
                    }
                }
            });
        }
    }

}
