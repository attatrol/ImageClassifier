package attatrol.neural.network;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.topology.LayeredTopologyGenerator;
import attatrol.neural.topology.Topology;

/**
 * Factory for neural networks.
 * @author attatrol
 *
 */
public final class NeuralNetworkFactory {

    /**
     * There can be a situation with error accumulation and subsequent network paralysis
     * if incoming values are bounded within a small range. So amplitude of incoming values
     * must not be lesser than 0.5 (this is arbitrary value, you may lower it at will)
     */
    public static final float INCOMING_VALUE_MINIMAL_BANDWIDTH = .5f;

    /**
     * Not in use
     */
    private NeuralNetworkFactory() { }

    /**
     * Factory method for a rumelhart perceptron, checks if options are valid.
     * @param settings used to set perceptron. 
     * @return an instance of rumelhart perceptron.
     * @throws IllegalArgumentException on invalid options.
     */
    public static NeuralNetwork getNetwork(NeuralNetworkSettings settings)
        throws NeuralNetworkGenerationException {
        // input and result vectors cardinality and amplitude checks
        if (settings.getInputVectorAmplitude() <= INCOMING_VALUE_MINIMAL_BANDWIDTH ) {
            throw new NeuralNetworkGenerationException("Incoming vector components have very small amplitude,"
                    + "increase the amplitude");
        }
        if (settings.getInputVectorSize() < 1) {
            throw new NeuralNetworkGenerationException("Input vector size is not a positive integer");
        }
        if (settings.getResultVectorSize() < 1) {
            throw new NeuralNetworkGenerationException("Resulting vector size is not a positive integer");
        }

        // topology checks
        if (settings.getTopologyType() == null) {
            throw new NeuralNetworkGenerationException("Null topology type");
        }
        final TopologySetting topologyType = settings.getTopologyType();
        Topology topology = null;
        if (topologyType == TopologySetting.PRESET_TOPOLOGY) {
            topology = settings.getTopology();
            if (topology == null) {
                throw new NeuralNetworkGenerationException("Preset topology is null");
            }
        }
        else if (topologyType == TopologySetting.PRESET_TOPOLOGY_DESCRIPTION) {
            topology = LayeredTopologyGenerator.generate(settings.getLayeredTopologyDescription());
        }
        else if (topologyType.containsLTDFactory()) {
            topology = LayeredTopologyGenerator.generate(topologyType.generateTopologyDescription(settings));
        }

        // processors checks
        if (settings.getAnalythicalProcessor() == null) {
            throw new NeuralNetworkGenerationException("Null analythical processor");
        }
        if (settings.getLearningProcessor() == null) {
            throw new NeuralNetworkGenerationException("Null learning processor");
        }
       return new NeuralNetwork(settings, topology);
    }

}
