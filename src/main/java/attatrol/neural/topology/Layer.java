package attatrol.neural.topology;

/**
 * Contains abstract description of some network layer to be generated.
 * @author attatrol
 *
 */
public class Layer {

    /**
     * Number of neurons in the layer
     */
    private final int neuronNumber;

    /**
     * Number of children for every neuron of current layer.
     * All children belong to the next layer of the network.
     */
    private final int childPerNeuronNumber;

    /**
     * Describes how children will be distributed amonng neurons of this layer.
     */
    private final LayerInterconnectionDistribution distribution;

    /**
     * Layer may be one of 3 types: layer of incoming vector,
     * ordinary (hidden) layer and surface layer where resulting vector is generated.
     */
    private final LayerType type;

    /**
     * Default ctor.
     * @param neuronNumber number of neurons in layer
     * @param childPerNeuronNumber number of child neurons in the nex layer per each neuron
     * @param distribution describes how child-parent connections between layers are distributed
     * @param type type of the layer
     */
    public Layer(int neuronNumber, int childPerNeuronNumber,
            LayerInterconnectionDistribution distribution, LayerType type) {
        this.neuronNumber = neuronNumber;
        this.childPerNeuronNumber = childPerNeuronNumber;
        this.distribution = distribution;
        this.type = type;
    }

    /**
     * @return number of neurons in the layer
     */
    public int getNeuronNumber() {
        return neuronNumber;
    }

    /**
     * @return number of child neurons in the nex layer per each neuron
     */
    public int getChildPerNeuronNumber() {
        return childPerNeuronNumber;
    }

    /**
     * @return describes how child-parent connections between layers are distributed
     */
    public LayerInterconnectionDistribution getDistribution() {
        return distribution;
    }

    /**
     * @return type of the layer
     */
    public LayerType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Layer = [neuronNumber=" + neuronNumber + ", childPerNeuronNumber="
                + childPerNeuronNumber + ", distribution=" + distribution + ", type=" + type + "]";
    }

}
