package attatrol.neural.topology;

/**
 * Type of the layer of the neural network
 * @author attatrol
 *
 */
public enum LayerType {
    /**
     * Layer of input vector, it is filled with coordinates of incoming vector
     */
    INPUT_VECTOR,
    /**
     * First neuron layer and all subsequent, except surface layer
     */
    ORDINARY,
    /**
     * Surface layer of neurons, where result vector is formed
     */
    SURFACE;

}
