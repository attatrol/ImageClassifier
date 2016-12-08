package attatrol.imageclassifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;

public class ImageClassifier implements Cloneable {

    public static final int MINIMAL_CLASS_SIZE = 10;

    private NavigableSet<ImageClass> classes = new TreeSet<>();

    private NavigableSet<String> classNames = new TreeSet<>();

    /**
     * Default ctor.
     */
    public ImageClassifier() { }

    /**
     * Copy constructor.
     * @param instance object to be copied
     */
    public ImageClassifier(ImageClassifier instance) {
        if (instance != null) {
            for (ImageClass iClass : instance.getClassesAsRows()) {
                addImageClass(iClass);
            }
        }
    }

    public void addImageClass(ImageClass iClass) throws IllegalArgumentException {
        if (classNames.contains(iClass.getClassName())) {
            throw new IllegalArgumentException(ImageClassifierI18nProvider.getText("message.classnameduplicate"));
        }
        if (classes.contains(iClass)) {
            throw new IllegalArgumentException(ImageClassifierI18nProvider.getText("message.classduplicate"));
        }
        if (iClass.getImageCount() < MINIMAL_CLASS_SIZE) {
            throw new IllegalArgumentException(String.format(ImageClassifierI18nProvider.getText("message.lownumberofimagesinclass"),
                    MINIMAL_CLASS_SIZE));
        }
        classes.add(iClass);
        classNames.add(iClass.getClassName());
    }

    public void removeImageClass(ImageClass iClass) throws IllegalArgumentException {
        classes.remove(iClass);
        classNames.remove(iClass.getClassName());
    }

    public List<ImageClass> getClassesAsRows() {
        List<ImageClass> rows = new ArrayList<>(classes);
        return rows;
    }


    public String[] getClassNames() {
        return classNames.toArray(new String[0]);
    }

    public List<LearningPair> getLearningPairs() throws ImageClassifierException {
            final int classCount = classes.size();
            double[][] reference = new double[classCount][classCount];
            int counter = 0;
            List<LearningPair> pairs = new ArrayList<>();
            Iterator<ImageClass> iter = classes.iterator();
            while (iter.hasNext()) {
                reference[counter][counter] = 1.;
                ImageClass iClass = iter.next();
                double[][] inputs = iClass.getInputVectors();
                for (double[] input : inputs) {
                    pairs.add(new LearningPair(input, reference[counter]));
                }
                counter++;
            }
            return pairs;
    }

    public class LearningPair {

        final double[] input;

        final double[] reference;

        public LearningPair(double[] input, double[] reference) {
            this.input = input;
            this.reference = reference;
        }

        public double[] getInput() {
            return input;
        }

        public double[] getReference() {
            return reference;
        }
    }

    public int getSize() {
        return classes.size();
    }
}
