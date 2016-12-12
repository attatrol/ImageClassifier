package attatrol.imageclassifier.imagehash;

import java.io.File;
import java.io.Serializable;

import attatrol.imageclassifier.ImageClassifierException;

public interface ImageHashFunction extends Serializable {

    double[] hash(File image) throws ImageClassifierException;

    int getResultSize();

}
