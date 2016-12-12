package attatrol.neural.learning.supervised;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.NeuralNetworkRuntimeException;
import attatrol.neural.analysis.AnalyticalProcessor;
import attatrol.neural.errorfunction.ErrorFunction;
import attatrol.neural.learning.LearningProcessor;
import attatrol.neural.network.NeuralNetworkState;

/**
 * This is a generic class for all supervised learners which try to minimize some
 * error function.
 * @author attatrol
 *
 */
public abstract class AbstractErrorMinimizerLearner implements LearningProcessor {

    /**
     * 
     */
    private static final long serialVersionUID = 8441388274838367331L;
    /**
     * Error function.
     */
    protected ErrorFunction errorFunction;

    /**
     * Default ctor.
     * @param errorFunction error function.
     */
    public AbstractErrorMinimizerLearner(ErrorFunction errorFunction) {
        this.errorFunction = errorFunction;
    }

    /**
     * {@inheritDoc}
     */
    public void process(NeuralNetworkState state, double[] reference, AnalyticalProcessor analyzer)
            throws NeuralNetworkRuntimeException {
        checkReferenceVector(reference, state.getResultVectorSize());
        internalProcess(state, reference, analyzer);
    }

    /**
     * @return associated error function.
     */
    public ErrorFunction getErrorFunction() {
        return errorFunction;
    }

    /**
     * {@inheritDoc}
     * Checks validity of error function.
     */
    @Override
    public void checkValidity() throws NeuralNetworkGenerationException {
        errorFunction.checkValidity();
    }

    /**
     * Performs supervised learning.
     * @param state network state
     * @param reference reference vector
     * @param analyzer analyzer associated with neural network
     * @throws NeuralNetworkRuntimeException thrown on some failure
     */
    protected abstract void internalProcess(NeuralNetworkState state, double[] reference, AnalyticalProcessor analyzer)
            throws NeuralNetworkRuntimeException;

    /**
     * Check if reference vector is valid.
     * @param reference reference vector
     * @throws NeuralNetworkRuntimeException on invalid reference vector
     */
    private void checkReferenceVector(double[] reference, int resultVectorSize) throws NeuralNetworkRuntimeException {
        if (reference.length != resultVectorSize) {
            throw new NeuralNetworkRuntimeException(
                    String.format("Reference vector has cardinality of %d, network accepts only %d",
                            reference.length, resultVectorSize));
        }
        for (int i = 0; i < reference.length; i++) {
            if (reference[i] < 0. || reference[i] > 1.) {
                throw new NeuralNetworkRuntimeException(
                        String.format("Reference vector coordinate %d has value of %f, which is out of bounds [0, 1]", 
                               i, reference[i]));
            }
        }
    }

    

}
