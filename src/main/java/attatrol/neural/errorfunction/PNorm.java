package attatrol.neural.errorfunction;

import attatrol.neural.NeuralNetworkGenerationException;

/**
 * Generalization of euclid norm (which is in base of
 * quadratic error function) is a p-norm.<p/>
 * Note it is much slower than quadratic error.
 * @author attatrol
 *
 */
public class PNorm implements ErrorFunction {

    /**
     * 
     */
    private static final long serialVersionUID = 8005683674298001118L;

    /**
     * p-norm coefficient
     */
    private final double p;

    private final double oneOverP;

    public PNorm(double p) {
        this.p = p;
        oneOverP = 1./p;
    }

    public double getP() {
        return p;
    }

    @Override
    public double getValue(double[] result, double[] reference) {
        double accumulator = 0.;
        for (int i = 0; i < result.length; i++) {
            accumulator += Math.pow(Math.abs(result[i] - reference[i]), p);
        }
        return Math.pow(accumulator,  oneOverP);
    }

    @Override
    public double getDerivative(double[] result, double[] reference, int indexOfResult) {
        double accumulator = 0.;
        for (int i = 0; i < result.length; i++) {
            accumulator += Math.pow(Math.abs(result[i] - reference[i]), p);
        }
        if (accumulator != 0.) {
            accumulator = Math.pow(accumulator, oneOverP - 1);
        }
        double temp = Math.abs(result[indexOfResult] - reference[indexOfResult]);
        if (temp != 0.) {
            accumulator *= Math.pow(temp, p - 1);
        }
        else {
            accumulator = 0.;
        }
        return result[indexOfResult] > reference[indexOfResult] ? accumulator : - accumulator;
    }

    @Override
    public void checkValidity() throws NeuralNetworkGenerationException {
        if ( p < 1.) {
            throw new NeuralNetworkGenerationException("PNorm: p must be greater than 1");
        }
    }

    @Override
    public String toString() {
        return "PNorm [p=" + p + "]";
    }

}
