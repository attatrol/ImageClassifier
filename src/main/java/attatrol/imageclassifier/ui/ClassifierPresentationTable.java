package attatrol.imageclassifier.ui;

import attatrol.imageclassifier.ImageClass;
import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClassifierPresentationTable extends TableView<ImageClass> {

    @SuppressWarnings("unchecked")
    ClassifierPresentationTable() {
        super();
        TableColumn<ImageClass, String> className = new TableColumn<>(
                ImageClassifierI18nProvider.getText("classifierpresentationtable.rowclassname"));
        className.setMinWidth(230);
        className.setCellValueFactory(
                new PropertyValueFactory<ImageClass, String>("className"));
 
        TableColumn<ImageClass, String> imageFolderPath = new TableColumn<>(
                ImageClassifierI18nProvider.getText("classifierpresentationtable.rowclasspath"));
        imageFolderPath.setMinWidth(640);
        imageFolderPath.setCellValueFactory(
                new PropertyValueFactory<ImageClass, String>("imageFolder"));

        TableColumn<ImageClass, Integer> imageCount = new TableColumn<>(
                ImageClassifierI18nProvider.getText("classifierpresentationtable.rowimagecount"));
        imageCount.setMinWidth(120);
        imageCount.setCellValueFactory(
                new PropertyValueFactory<ImageClass, Integer>("imageCount"));

        setRowFactory( tv -> {
            TableRow<ImageClass> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ImageClass rowData = row.getItem();
                    rowData.getImageGallery().show();
                }
            });
            return row ;
        });

        this.getColumns().addAll(className, imageFolderPath, imageCount);

        // this.setSelectionModel();

    }

}
