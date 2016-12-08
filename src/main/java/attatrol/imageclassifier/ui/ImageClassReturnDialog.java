package attatrol.imageclassifier.ui;

import java.io.File;
import java.io.IOException;

import attatrol.imageclassifier.ImageClass;
import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;
import attatrol.imageclassifier.imagehash.ImageHashFunction;
import attatrol.neural.ui.javafx.misc.GenericValueReturnDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

public class ImageClassReturnDialog extends GenericValueReturnDialog<ImageClass>{

    private static File oldFileDirectory;

    private File selectedDirectory;

    private ImageHashFunction hashFunction;

    private ImageClass result;

    private Button selectFolderButton = new Button(ImageClassifierI18nProvider
            .getText("imageclassreturmdialog.selectfolderbuttonname"));
    {
        selectFolderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedDirectory = dirChooser.showDialog(null);
                if (selectedDirectory != null) {
                    final File parentDirectory = selectedDirectory.getParentFile();
                    oldFileDirectory = parentDirectory == null
                            ? selectedDirectory : parentDirectory;
                    dirChooser.setInitialDirectory(oldFileDirectory);
                    try {
                        chosenDirectoryFilename
                            .setText(ImageClassifierI18nProvider.getText("imageclassreturmdialog.filenamelabel")
                                + selectedDirectory.getCanonicalPath().toString());
                    } catch (IOException e) {
                        chosenDirectoryFilename.setText(ImageClassifierI18nProvider.getText("imageclassreturmdialog.filenameerror"));
                    }
                }
            }
        });
    }

    private TextField className = new TextField();

    private Label chosenDirectoryFilename = new Label(ImageClassifierI18nProvider.getText("imageclassreturmdialog.emptyfilenamelabel"));

    private DirectoryChooser dirChooser = new DirectoryChooser();
    {
        dirChooser.setTitle(ImageClassifierI18nProvider.getText("imageclassreturmdialog.dirchoosertitle"));
        if (oldFileDirectory != null) {
            dirChooser.setInitialDirectory(oldFileDirectory);
        }
    }

    public ImageClassReturnDialog(ImageHashFunction hashFunction) {
        super();
        this.hashFunction = hashFunction;
        setContent();
    }

    private void setContent() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Label classNameInfo = new Label(ImageClassifierI18nProvider
                .getText("imageclassreturmdialog.enterclassnamelabel"));
        grid.add(classNameInfo, 0, 0, 2, 1);
        className.setMinWidth(120);
        grid.add(className, 0, 1, 2, 1);
        Label classFolderInfo = new Label(ImageClassifierI18nProvider
                .getText("imageclassreturmdialog.selectclassfolderlabel"));
        grid.add(classFolderInfo, 0, 3, 2, 1);
        grid.add(selectFolderButton, 0, 4, 2, 1);
        grid.add(chosenDirectoryFilename, 0, 5, 3, 1);
        grid.setMinWidth(700);
        setTitle(ImageClassifierI18nProvider.getText("imageclassreturmdialog.title"));
        this.getDialogPane().setContent(grid);
    }

    @Override
    protected ImageClass createResult() {
        return result;
    }

    @Override
    protected void validate() throws Exception {
        result = ImageClass.createImageClass(selectedDirectory, className.getText(), hashFunction);
    }

}