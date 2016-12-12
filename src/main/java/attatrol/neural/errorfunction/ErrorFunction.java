package attatrol.neural.errorfunction;

import java.io.Serializable;

import attatrol.neural.NeuralNetworkGenerationException;

/**
 * Error function is a function which must be minimized
 * during backpropagation learning (or other algorithm of learning).
 * Error function takes resulting vector and reference vector as arguments.
 * Usually error function is a some kind of measure between both argument vectors.
 * Non-trivial error function for certain tasks may enhance time of converging for
 * learning algorithm dramatically.
 * @author attatrol
 *
 */
public interface ErrorFunction extends Serializable {

    /**
     * Calculates value of the error function.
     * @param result resulting vector from neural network
     * @param reference reference vector from teacher
     * @return value
     */
    double getValue(double[] result, double[] reference);

    /**
     * Calculates value of the first partial derivative
     * with respect to result[indexOfResult]
     * @param result result vector of neural network
     * @param reference reference result vector
     * @param indexOfResult index of result 
     * @return
     */
    double getDerivative(double[] result, double[] reference, int indexOfResult);

    /**
     * Checks if function has proper internal state.
     * Should be performed once before it will be used.
     */
    void checkValidity() throws NeuralNetworkGenerationException;


}
