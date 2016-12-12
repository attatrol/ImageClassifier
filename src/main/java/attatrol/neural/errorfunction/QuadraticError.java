package attatrol.neural.errorfunction;

import attatrol.neural.NeuralNetworkGenerationException;

/**
 * Classical example of an error function for 
 * perceptron is quadratic error function. However you are free to
 * implement other error functions.
 * @author attatrol
 *
 */
public class QuadraticError implements ErrorFunction {

    /**
     * 
     */
    private static final long serialVersionUID = 1764209472400171543L;

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValue(double[] result, double[] reference) {
        double accumulator = 0;
        for (int i = 0; i < result.length; i++) {
            accumulator += (result[i] - reference[i]) * (result[i] - reference[i]);
        }
        return 0.5 * accumulator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDerivative(double[] result, double[] reference, int indexOfResult) {
        return result[indexOfResult] - reference[indexOfResult];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkValidity() throws NeuralNetworkGenerationException {
        // nothing to do: no internal state
        
    }

    @Override
    public String toString() {
        return "QuadraticError";
    }
}
