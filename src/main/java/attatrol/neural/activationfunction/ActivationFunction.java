package attatrol.neural.activationfunction;

import attatrol.neural.NeuralNetworkGenerationException;

/**
 * This is an interface for some activation function.
 * Its domain is a set of real numbers.
 * Its codomain MUST be [0, 1] and it is your resulting vector codomain.
 * It should be a monotonic (increasing) function.
 * It should tend to 0 (or 1) when argument tends to -inf,
 * and it should tend to 1 (or 0) when argument tends to +inf,
 * it should be a differentiable function for consistency with backpropagation algorithm.
 * @author attatrol
 *
 */
public interface ActivationFunction {

    /**
     * Calculates function value. Must be in [0, 1].
     * @param arg argument
     * @return value
     */
    abstract double getValue(double arg);

    /**
     * Calculates first derivative of this function.
     * @param arg argument.
     * @param funcValue calculated value of the function
     * @return first derivative
     */
    abstract double getDerivative(double arg, double funcValue);

    /**
     * Checks if function has proper internal state.
     * Should be performed once before it will be used.
     */
    abstract void checkValidity() throws NeuralNetworkGenerationException;
}
