package attatrol.neural.analysis;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.learning.LearningProcessor;
import attatrol.neural.network.NeuralNetworkState;

/**
 * Analythical processor calculates result values for all neurons.
 * Ensure that there is only one client neural network for each mutable processor.
 * @author attatrol
 *
 */
public interface AnalyticalProcessor {

    /**
     * Calculates linear combinations and result values for all neurons.
     * Incoming vector must be mapped into result array before processing.
     * @param state state of some neural network
     * @param learner theoretically learner and analyzer may depend on each other internal state
     */
    void process(NeuralNetworkState state, LearningProcessor learner);

    /**
     * Use this before usage to check if internal state of processor is valid
     * @throws NeuralNetworkGenerationException on invalidity of processor
     */
    void checkValidity() throws NeuralNetworkGenerationException;

}
