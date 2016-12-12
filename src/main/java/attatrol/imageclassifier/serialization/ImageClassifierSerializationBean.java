package attatrol.imageclassifier.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import attatrol.imageclassifier.ImageClassifier;
import attatrol.imageclassifier.imagehash.ImageHashFunction;
import attatrol.neural.analysis.AnalyticalProcessor;
import attatrol.neural.learning.LearningProcessor;
import attatrol.neural.network.NeuralNetwork;
import attatrol.neural.network.NeuralNetworkState;
import attatrol.neural.ui.javafx.misc.UiUtils;

/**
 * Bean used for serialization/deserialization of internal state of the
 * {@link attatrol.imageclassifier.ui.MainForm}
 * @author attatrol
 *
 */
public class ImageClassifierSerializationBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7890864409582332339L;

    private ImageHashFunction hashFunction;

    private ImageClassifier classifier;

    private NeuralNetworkState neuralNetworkState;

    private LearningProcessor learningProcessor;

    private AnalyticalProcessor analyticalProcessor;

    public ImageClassifierSerializationBean(ImageHashFunction hashFunction,
            ImageClassifier classifier, NeuralNetwork network) {
        this.hashFunction = hashFunction;
        this.classifier = classifier;
        neuralNetworkState = network.getNetworkStateCopy();
        learningProcessor = network.getLearningProcessor();
        analyticalProcessor = network.getAnalythicalProcessor();
    }

    public ImageClassifier getClassifier() {
        return classifier;
    }

    
    public ImageHashFunction getHashFunction() {
        return hashFunction;
    }

    public NeuralNetwork getNeuralNetwork() {
        return new NeuralNetwork(neuralNetworkState, analyticalProcessor, learningProcessor);
    }

    public static void serialize(ImageHashFunction hashFunction,
            ImageClassifier classifier, NeuralNetwork network, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, false))) {
            oos.writeObject(new ImageClassifierSerializationBean(hashFunction, classifier, network));
        }
    }

    public static ImageClassifierSerializationBean deserialize(File file)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        ImageClassifierSerializationBean result = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            result = (ImageClassifierSerializationBean) ois.readObject();
        }
        return result;
    }

}
