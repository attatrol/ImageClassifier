package attatrol.neural.analysis;

import java.util.Random;

import attatrol.neural.activationfunction.ActivationFunction;

/**
 * Stochastic analyzer is a modification of a classic activation function analyzer
 * that uses stochastic transfer functions based on original activation function.<p/>
 * @author attatrol
 *
 */
public class StochasticActivationFunctionAnalyzer extends ActivationFunctionAnalyzer {

    /**
     * Source of random numbers.
     */
    private Random random = new Random();

    /**
     * Default ctor.
     * @param activationFunction activation function
     */
    public StochasticActivationFunctionAnalyzer(ActivationFunction activationFunction) {
        super(activationFunction);
    }

    /**
     * {@inheritDoc} <p/>
     * Activation function is wrapped with stochastic transfer function.
     */
    @Override
    protected double getResult(double linearCombo) {
        final double actiationFunctionValue = activationFunction.getValue(linearCombo);
        return actiationFunctionValue > random.nextDouble() ? 1. : 0.;
    }

    @Override
    public String toString() {
        return "StochasticActivationFunctionAnalyzer [activationFunction=" + activationFunction
                + "]";
    }

    
}
