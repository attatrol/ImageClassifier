package attatrol.imageclassifier.ui;


import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * A simple ui for mushroom test.
 *
 * @author attatrol
 *
 */
public class ImageClassifierApplication extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new MainForm());
        primaryStage.show();
        primaryStage.setTitle(ImageClassifierI18nProvider.getText("mainform.title"));
    }

    /**
     * Entry point
     * @param strings not in use
     */
    public static final void main(String...strings) {
        launch(strings);
    }

}

