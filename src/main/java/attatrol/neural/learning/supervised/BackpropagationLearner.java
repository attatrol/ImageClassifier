package attatrol.neural.learning.supervised;

import java.util.Arrays;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.NeuralNetworkRuntimeException;
import attatrol.neural.activationfunction.ActivationFunction;
import attatrol.neural.analysis.ActivationFunctionAnalyzer;
import attatrol.neural.analysis.AnalyticalProcessor;
import attatrol.neural.errorfunction.ErrorFunction;
import attatrol.neural.network.NeuralNetworkState;

public class BackpropagationLearner extends AbstractErrorMinimizerLearner {

    /**
     * 
     */
    private static final long serialVersionUID = -3690211285958660008L;

    /**
     * This factor influences overall change speed of neuron weights.
     * In other projects i saw its value between 0.1 and 0.05.
     * Generally speaking, if your learning set is large enough,
     * you should keep this as low as possible.
     * <p>
     * Lower it if you experience network paralysis.
     */
    private float changeFactor;

    /**
     * Default ctor.
     * @param errorFunction error function
     * @param changeFactor weight shift speed modifier, between 0 to 1
     */
    private BackpropagationLearner(ErrorFunction errorFunction, float changeFactor) {
        super(errorFunction);
        this.changeFactor = changeFactor;
    }

    /**
     * Simple ctor. sets change factor to 0.05.
     * @param errorFunction error function
     */
    public BackpropagationLearner(ErrorFunction errorFunction) {
        super(errorFunction);
        this.changeFactor = 0.05f;
    }

    /**
     * Factory method for backpropagation learner.
     * @param errorFunction error function
     * @param changeFactor change factor
     * @return backpropagation learner instance
     * @throws NeuralNetworkGenerationException on invalid parameters
     */
    public static BackpropagationLearner getBackpropagationLearner(ErrorFunction errorFunction,
            float changeFactor) throws NeuralNetworkGenerationException {
        if (changeFactor <= 0 || changeFactor > 1) {
            throw new NeuralNetworkGenerationException("Change factor must be in (0, 1]");
        }
        return new BackpropagationLearner(errorFunction, changeFactor);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Backward propagation of error algorithm.<br/>
     * Legend for in-code commentaries:<br/>
     * E -error function<br/>
     * S[i] - linear combination of neuron i<br/>
     * R[i] - result of neuron i (R[i] = f(S[i]), where f is an activation function)<br/>
     * w[i][j] - weight of neuron i which factors with result of vector j</br>
     * b[i] - bias of neuron i</br>
     * n - change factor, some small factor between 0 and 1 which affects speed of weight change
     */
    @Override
    protected void internalProcess(NeuralNetworkState state, double[] reference, AnalyticalProcessor analyzer)
            throws NeuralNetworkRuntimeException {
        int[] traverseOrder = state.getTraverseOrder();
        float[][] weight = state.getWeight();
        float[] bias = state.getBias();
        int[][]parents = state.getParents();
        double[] result = state.getResult();
        double[] linearCombination = state.getLinearCombination();
        if (!(analyzer instanceof ActivationFunctionAnalyzer)) {
            throw new NeuralNetworkRuntimeException("Backpropagation learner works only with analyzers"
                    + "which use differentiable activation function (belonging to ActivationFunctionAnalylizer class)");
        }
        final ActivationFunctionAnalyzer afa = (ActivationFunctionAnalyzer) analyzer;
        final ActivationFunction f = afa.getActivationFunction();

        // array of dE/dS[i]
        double[] errorFunctionDerivative = new double[result.length];

        int surfaceLayerFirstNeuronIndex = result.length - reference.length;
        // neural network result vector (results of the surface layer)
        double[] resultVector = Arrays.copyOfRange(result, surfaceLayerFirstNeuronIndex, result.length);

        // first let us find all dE/dS for surface layer, it is easy:
        // dE/dS[i] = dE/dR[i] * dR[i]/dS[i] = dE/dR[i] * df(S[i])/dS[i]
        for (int i = 0; i < reference.length; i++) {
            errorFunctionDerivative[i + surfaceLayerFirstNeuronIndex] = 
                    errorFunction.getDerivative(resultVector, reference, i)
                    * f.getDerivative(linearCombination[i + surfaceLayerFirstNeuronIndex], resultVector[i]);
        }
        // now let us process neurons in backwards order
        for (int j = traverseOrder.length - 1; j >= 0; j--) {
            // for this neuron we already have dE/dS[i]
            double efd = errorFunctionDerivative[traverseOrder[j]];
            // i - current neuron index
            final int i = traverseOrder[j];
            for (int k = 0; k < weight[i].length; k++) {
                // j = parents[i][k] - index of parent connected to weight w[i][j]
                final int parentIndex = parents[i][k];
                // for any non-surface neuron m dE/dS[m] = SUM (dE/dS[i] * dS[i]/dS[m]),
                // where i runs through all indexes of children of neuron m
                // here m = parentIndex
                // we have to add to errorFunctionDerivative[parentIndex]
                // summand dE/dS[i] * dS[i]/dS[m] = edf * dS[i]/dR[m] * dR[m]/dS[m] =
                // = edf * w[i][m] * df(S[m])/dS[m]
                errorFunctionDerivative[parentIndex] += efd * weight[i][k]
                        * f.getDerivative(linearCombination[parentIndex], result[parentIndex]);
                // Now actial weight change
                // w[i][j] -= n * dE/dw[i][j],
                // dE/dw[i][j] = dE/dS[i] * dS[i]/dw[i][j] = dE/dS[i] * R[j]
                weight[i][k] -= changeFactor * efd * result[parentIndex];
            }
            // b[i]  -= n * dE/db[i]
            // dE/db[i] = dE/dS[i] * dS[i]/db[i] = dE/dS[i] * 1 = dE/dS[i]
            bias[i] -= changeFactor * efd;
        }
    }

    @Override
    public String toString() {
        return "BackpropagationLearner [changeFactor=" + changeFactor + ", errorFunction="
                + errorFunction + "]";
    }

}
