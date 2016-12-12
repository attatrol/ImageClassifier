package attatrol.neural.activationfunction;

import attatrol.neural.NeuralNetworkGenerationException;

/**
 * Sigmoid function f(x) = 1 / (1 + exp(- a * x)), a != 0
 * @author attatrol
 *
 */
public class SigmoidFunction implements ActivationFunction {

    /**
     * 
     */
    private static final long serialVersionUID = 857735048222522758L;
    /**
     * flatness ratio. lays between 0 and +inf, lower value mkes function flatter.
     * Inverts function if lays between -inf and 0. If value is zeroed then
     * neural network will fail to work. However last two cases are explicitly forbidden.
     */
    private float a;

    /**
     * Constructor for the logistic function f(x) = 1 / (1 + exp(-x)).
     */
    public SigmoidFunction() {
        this.a = 1.f;
    }

    /**
     * General constructor of sigmoid function.
     * @param a flatness ratio, must be a positive number
     */
    public SigmoidFunction(float a) {
        this.a = a;
    }

    /**
     * {@inheritDoc}
     * Calculates value of the sigmoid function.
     */
    @Override
    public double getValue(double arg) {
        return 1. / (1. + Math.exp(-1. * a * arg));
    }

    /**
     * {@inheritDoc}
     * Calculates value of the first derivative of the sigmoid function.
     */
    @Override
    public double getDerivative(double arg, double funcValue) {
        return a * funcValue * (1 - funcValue);
    }

    /**
     * {@inheritDoc}
     * Throws exception if flatness ratio is not a positive number.
     */
    @Override
    public void checkValidity() throws NeuralNetworkGenerationException {
        if (a <= 0.f) {
            throw new NeuralNetworkGenerationException("Flatness ratio of sigmoid function must be a positive number");
        }
    }

    @Override
    public String toString() {
        return "SigmoidFunction [flatness ratio=" + a + "]";
    }
}
