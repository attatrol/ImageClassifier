package attatrol.neural.learning;

import java.io.Serializable;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.NeuralNetworkRuntimeException;
import attatrol.neural.analysis.AnalyticalProcessor;
import attatrol.neural.network.NeuralNetworkState;

/**
 * Learning processor for neuron network is a special operator which updates
 * state of neural network (changes values of weights and biases).
 * Its purpose is to converge neural network to ideal function that 
 * maps set of incoming vectors into set of resulting vectors without error.
 * <p>
 * Ensure that there is only one client neural network for each mutable processor.
 * @author attatrol
 *
 */
public interface LearningProcessor extends Serializable {

    /**
     * Modifies internal state of neural network after analytical processor.
     * Incoming vector must be mapped into result array before processing.
     * traverseOrder, weight, bias, parents, children parameters may be modified.

     * @param state state of some neural network
     * @param reference reference result vector, null if learning is not supervised
     * @param analyzer theoretically learner and analyzer may depend on each other internal state
     * @throws NeuralNetworkRuntimeException on some error during learning 
     */
    void process(NeuralNetworkState state, double[] reference, AnalyticalProcessor analyzer)
            throws NeuralNetworkRuntimeException;

    /**
     * Checks if function has proper internal state.
     * Should be performed once before it will be used.
     */
    void checkValidity() throws NeuralNetworkGenerationException;

}
