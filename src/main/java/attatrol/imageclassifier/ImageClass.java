package attatrol.imageclassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.imageio.ImageIO;

import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;
import attatrol.imageclassifier.imagehash.ImageHashFunction;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Describes some image class
 * @author attatrol
 *
 */
public class ImageClass implements Comparable<ImageClass> {

    /**
     * Filters files with supported formats.
     */
    public static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        private final String[] supportedFormats = ImageIO.getReaderFileSuffixes();

        @Override
        public boolean accept(File dir, String name) {
            for (final String ext : supportedFormats) {
                if (name.endsWith("." + ext)) {
                    return true;
                }
            }
            return false;
        }

    };

    /**
     * Folder that contains the image
     */
    private File imageFolder;

    /**
     * Name of the class
     */
    private String className;

    /**
     * Hash function used to produce input vectors from images.
     */
    private ImageHashFunction hashFunction;

    /**
     * Image files
     */
    private File[] imageFiles;

    /**
     * Hidden simple ctor.
     * @param imageFolder folder with class images
     * @param className name of the class
     * @param hashFunction hash function that maps image into input vector
     */
    private ImageClass(File imageFolder, String className, ImageHashFunction hashFunction) {
        this.imageFolder = imageFolder;
        this.className = className;
        this.hashFunction = hashFunction;
        imageFiles = imageFolder.listFiles(IMAGE_FILTER);
        Arrays.sort(imageFiles);
    }

    /**
     * Factory method used to create an image class
     * @param imageFolder folder with class images
     * @param className name of this class
     * @param hashFunction hash function that maps images into input vectors
     * @return image class instance
     * @throws IllegalArgumentException on invalid input parametres
     */
    public static ImageClass createImageClass(File imageFolder, String className, ImageHashFunction hashFunction)
            throws IllegalArgumentException {
        if (imageFolder == null) {
            throw new IllegalArgumentException(ImageClassifierI18nProvider.getText("message.nullimagefolder"));
        }
        if (!imageFolder.canRead()) {
            throw new IllegalArgumentException(ImageClassifierI18nProvider.getText("message.ioerrorimagefolder"));
        }
        if (!imageFolder.isDirectory()) {
            throw new IllegalArgumentException(ImageClassifierI18nProvider.getText("message.imagefoldernotdirectory"));
        }
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException(ImageClassifierI18nProvider.getText("message.emptyclassname"));
        }
        if (hashFunction == null) {
            throw new IllegalArgumentException(ImageClassifierI18nProvider.getText("message.nullhashfunction"));
        }
        return new ImageClass(imageFolder, className, hashFunction);
    }

    /**
     * Checks if class is unchanged.
     * @return true if all images of the class are present
     */
    public boolean validate() {
        File[] newImageFiles = imageFolder.listFiles(IMAGE_FILTER);
        Arrays.sort(newImageFiles);
        return Arrays.equals(newImageFiles, imageFiles);
    }

    /**
     * Creates preview for all images that belong to the class
     * @return scroll pane with images
     * @throws Exception on failure po produce scroll pane
     */
    public ScrollPane getPreview() throws Exception {
        ScrollPane root = new ScrollPane();
        TilePane tile = new TilePane();
        root.setStyle("-fx-background-color: DAE6F3;");
        tile.setPadding(new Insets(15, 15, 15, 15));
        tile.setHgap(15);

        for (final File file : imageFiles) {
            ImageView imageView = createImageView(file);
            tile.getChildren().addAll(imageView);
        }

        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
        root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical
        root.setFitToWidth(true);
        root.setContent(tile);
        return root;
    }

    /**
     * {@inheritDoc}
     * Chacks if 2 classes use the same images
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ImageClass) {
            ImageClass iClass = (ImageClass) other;
            if (iClass.getImageFolder() == this.imageFolder) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return image folder
     */
    public File getImageFolder() {
        return imageFolder;
    }

    /**
     * @return class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return image count
     */
    public int getImageCount() {
        return imageFiles.length;
    }

    /**
     * @return input vectors
     * @throws ImageClassifierException
     */
    public double[][] getInputVectors() throws ImageClassifierException {
        double[][] result = new double[imageFiles.length][];
        for (int i = 0; i < imageFiles.length; i++) {
            result[i] = hashFunction.hash(imageFiles[i]);
        }
        return result;
    }

    public ImageGallery getImageGallery() {
        try {
            return new ImageGallery();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ImageClass o) {
        return imageFolder.compareTo(o.getImageFolder());
    }

    /**
     * Creates clickable thumbnail for an image
     * @param imageFile image file
     * @return image view (thumbnail)
     * @throws FileNotFoundException if image doesn't exist
     */
    private static ImageView createImageView(final File imageFile) throws FileNotFoundException {

        ImageView imageView = null;
        final Image image = new Image(new FileInputStream(imageFile), 150, 0, true, true);
        imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {//TODO WeakEventHandler

            @Override
            public void handle(MouseEvent mouseEvent) {

                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

                    if (mouseEvent.getClickCount() == 2) {
                        try {
                            BorderPane borderPane = new BorderPane();
                            ImageView imageView = new ImageView();
                            Image image = new Image(new FileInputStream(imageFile));
                            imageView.setImage(image);
                            imageView.setStyle("-fx-background-color: BLACK");
                            imageView.setFitHeight(50);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setCache(true);
                            borderPane.setCenter(imageView);
                            borderPane.setStyle("-fx-background-color: BLACK");
                            Stage newStage = new Stage();
                            newStage.setWidth(120);
                            newStage.setHeight(80);
                            newStage.setTitle(imageFile.getName());
                            Scene scene = new Scene(borderPane, Color.BLACK);
                            newStage.setScene(scene);
                            newStage.show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        return imageView;
    }

    public class ImageGallery extends Stage {

        public ImageGallery() throws FileNotFoundException {
            ScrollPane root = new ScrollPane();
            TilePane tile = new TilePane();
            root.setStyle("-fx-background-color: DAE6F3;");
            tile.setPadding(new Insets(15, 15, 15, 15));
            tile.setHgap(15);

            for (final File file : imageFiles) {
                    ImageView imageView = createImageView(file);
                    tile.getChildren().addAll(imageView);
            }

            root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
            root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            root.setFitToWidth(true);
            root.setContent(tile);

            setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            setHeight(Screen.getPrimary().getVisualBounds()
                    .getHeight());
            Scene scene = new Scene(root);
            setScene(scene);

        }
    }

}
