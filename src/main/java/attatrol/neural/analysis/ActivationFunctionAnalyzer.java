package attatrol.neural.analysis;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.activationfunction.ActivationFunction;
import attatrol.neural.learning.LearningProcessor;
import attatrol.neural.network.NeuralNetworkState;

/**
 * Uses some activation function on linear combination for each neuron.
 * @author attatrol
 *
 */
public class ActivationFunctionAnalyzer implements AnalyticalProcessor{

    /**
     * Activation function, maps linear combination in [0, 1].
     */
    protected ActivationFunction activationFunction;

    /**
     * Default ctor.
     * @param activationFunction activation function
     */
    public ActivationFunctionAnalyzer(ActivationFunction activationFunction) {
        super();
        this.activationFunction = activationFunction;
    }

    /**
     * @return activation function of this analyzer
     */
    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    /**
     * {@inheritDoc}
     * Traverses over neurons, calculates linear combination,
     * then applies activation function to it.
     */
    @Override
    public void process(NeuralNetworkState state, LearningProcessor learner) {
        int[] traverseOrder = state.getTraverseOrder();
        float[][] weight = state.getWeight();
        float[] bias = state.getBias();
        int[][] parents = state.getParents();
        double[] linearCombination = state.getLinearCombination();
        double[] result = state.getResult();
        for (int i = 0; i < traverseOrder.length; i++) {
            final int currentNeuronIndex = traverseOrder[i];
            double linearCombo = bias[currentNeuronIndex];
            for (int j = 0; j < parents[currentNeuronIndex].length; j++) {
                linearCombo += weight[currentNeuronIndex][j]
                        * result[parents[currentNeuronIndex][j]];
            }
            linearCombination[currentNeuronIndex] = linearCombo;
            result[currentNeuronIndex] = getResult(linearCombo);
        }
        
    }

    /**
     * {@inheritDoc}
     * Checks validity of activation function
     */
    @Override
    public void checkValidity() throws NeuralNetworkGenerationException {
        if (activationFunction == null) {
            throw new NeuralNetworkGenerationException("Activation function of analyzer is null");
        }
        activationFunction.checkValidity();
    }

    /**
     * A hook for a {@link StochasticActivationFunctionAnalyzer}.
     * Very possible that this hook will be used to produce other kinds of analyzers,
     * possibly recurrent ones.
     * @param linearCombo
     * @return
     */
    protected double getResult(double linearCombo) {
        return activationFunction.getValue(linearCombo);
    }

    @Override
    public String toString() {
        return "ActivationFunctionAnalyzer [activationFunction=" + activationFunction + "]";
    }
}
