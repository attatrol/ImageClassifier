package attatrol.imageclassifier.imagehash;

import java.io.File;

import attatrol.imageclassifier.ImageClassifierException;

public interface ImageHashFunction {

    double[] hash(File image) throws ImageClassifierException;

    int getResultSize();

}
