package attatrol.neural.network;

import attatrol.neural.analysis.AnalyticalProcessor;
import attatrol.neural.learning.LearningProcessor;
import attatrol.neural.topology.LayeredTopologyDescription;
import attatrol.neural.topology.Topology;

/**
 * POJO, contains all necessary data for generation of a neural network.
 * @author attatrol
 *
 */
public class NeuralNetworkSettings {

    /**
     * Cardinality of the input vector
     */
    private final int inputVectorSize;

    /**
     * Cardinality of the result vector
     */
    private final int resultVectorSize;

    /**
     * Incoming vector coordinates must be non-negative numbers
     * and they must not exceed this bound. This is used for 
     * proper generations of biases for neurons.
     */
    private final double inputVectorAmplitude;

    /**
     * Describes how will the topology be produced
     */
    private final TopologySetting topologyType;

    /**
     * Preset layered topology description
     */
    private final LayeredTopologyDescription topologyDescription;

    /**
     * Preset topology
     */
    private final Topology topology;

    /**
     * Analytical processor
     */
    private final AnalyticalProcessor analyticalProcessor;

    /**
     * Learning processor
     */
    private final LearningProcessor learningProcessor;
 

    /**
     * Ctor used with preset topology type.
     * @param inputVectorSize incoming vector size. 
     * @param resultVectorSize resulting vector size.
     * @param incomingVectorAmplitude maximal possible value for any coordinate of any incoming vector.
     * @param topologyType topology of the network to be generated
     * @param analyticalProcessor analytical processor used to map input vector into resulting vector
     * @param learningProcessor learning processor used to learn the network
     */
    public NeuralNetworkSettings(int inputVectorSize, int resultVectorSize,
            double incomingVectorAmplitude, TopologySetting topologyType,
            AnalyticalProcessor analyticalProcessor, LearningProcessor learningProcessor) {
        this.inputVectorSize = inputVectorSize;
        this.resultVectorSize = resultVectorSize;
        this.inputVectorAmplitude = incomingVectorAmplitude;
        this.topologyType = topologyType;
        this.topology = null;
        this.topologyDescription = null;
        this.learningProcessor = learningProcessor;
        this.analyticalProcessor = analyticalProcessor;
    }

    /**
     * Ctor used with preset topology.
     * Remember, there is no internal check on topology defects, network with
     * invalid topology may enter infinite cycle or perform poorly.
     * Use this ctor for TEST purposes only.
     * @param inputVectorAmplitude maximal possible value for any coordinate of any incoming vector.
     * @param topology preset topology of the network to be generated
     * @param analyticalProcessor analytical processor used to map input vector into resulting vector
     * @param learningProcessor learning processor used to learn the network
     */
    public NeuralNetworkSettings(double inputVectorAmplitude,
            Topology topology, AnalyticalProcessor analyticalProcessor, LearningProcessor learningProcessor) {
        this.inputVectorSize = topology.getNeuronFirstIndex();
        this.resultVectorSize = topology.getSourceTotalNumber() - topology.getSurfaceNeuronFirstIndex() + 1;
        this.inputVectorAmplitude = inputVectorAmplitude;
        this.topologyType = TopologySetting.PRESET_TOPOLOGY;
        this.topology = topology;
        this.topologyDescription = null;
        this.learningProcessor = learningProcessor;
        this.analyticalProcessor = analyticalProcessor;
    }

    /**
     * Ctor used with preset topology description. User sets arbitrary layered topology description,
     * guaranteed that validity checks of this description will be performed during topology generation.
     * @param inputVectorAmplitude maximal possible value for any coordinate of any incoming vector.
     * @param topologyDescription preset layered topology description of the network to be generated
     * @param analyticalProcessor analytical processor used to map input vector into resulting vector
     * @param learningProcessor learning processor used to learn the network
     */
    public NeuralNetworkSettings(double inputVectorAmplitude, LayeredTopologyDescription topologyDescription,
            AnalyticalProcessor analyticalProcessor, LearningProcessor learningProcessor) {
        this.inputVectorSize = topologyDescription.getLayers().get(0).getNeuronNumber();
        this.resultVectorSize = topologyDescription.getLayers()
                .get(topologyDescription.getLayers().size() - 1).getNeuronNumber();
        this.inputVectorAmplitude = inputVectorAmplitude;
        this.topologyType = TopologySetting.PRESET_TOPOLOGY_DESCRIPTION;
        this.topology = null;
        this.topologyDescription = topologyDescription;
        this.learningProcessor = learningProcessor;
        this.analyticalProcessor = analyticalProcessor;
    }

    public int getInputVectorSize() {
        return inputVectorSize;
    }

    public int getResultVectorSize() {
        return resultVectorSize;
    }

    public double getInputVectorAmplitude() {
        return inputVectorAmplitude;
    }

    public TopologySetting getTopologyType() {
        return topologyType;
    }

    public Topology getTopology() {
        return topology;
    }

    public LayeredTopologyDescription getLayeredTopologyDescription() {
        return topologyDescription;
    }

    public AnalyticalProcessor getAnalythicalProcessor() {
        return analyticalProcessor;
    }

    public LearningProcessor getLearningProcessor() {
        return learningProcessor;
    }

}
