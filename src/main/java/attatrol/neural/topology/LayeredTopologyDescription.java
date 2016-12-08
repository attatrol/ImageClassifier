package attatrol.neural.topology;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import attatrol.neural.NeuralNetworkGenerationException;

/**
 * Blueprint for generation of a custom network topology (network graph)
 * @author attatrol
 *
 */
public class LayeredTopologyDescription {

    /**
     * Layers in order of appearance.
     */
    private final List<Layer> layers = new ArrayList<>();

    /**
     * Adds new layer.
     * @param layer new layer.
     * @throws NeuralNetworkGenerationException on bad 
     */
    public void addLayer(Layer layer) throws NeuralNetworkGenerationException {
        if (layers.isEmpty() && layer.getType() != LayerType.INPUT_VECTOR) {
            throw new NeuralNetworkGenerationException("First layer must be incoming vector layer");
        }
        else if (!layers.isEmpty() && layer.getType() == LayerType.INPUT_VECTOR) {
            throw new NeuralNetworkGenerationException("Incoming vector layer already exist, can not add another one");
        }
        else if (!layers.isEmpty() && layers.get(layers.size() - 1).getType() == LayerType.SURFACE) {
            throw new NeuralNetworkGenerationException("Last layer is a surface layer, no other layers may be added");
        }
        else if (layer.getChildPerNeuronNumber() < 1) {
            throw new NeuralNetworkGenerationException(
                    String.format("Layer %d: Number of children per neuron must be a positive integer", layers.size()));
        }
        else if (layer.getNeuronNumber() < 1) {
            throw new NeuralNetworkGenerationException(
                    String.format("Layer %d: Number of neurons (or incoming vector cardinality) must be positive integer",
                            layers.size()));
        }
        else if (!layers.isEmpty()) {
            final int childrenPerParentLayerNeuron = layers.get(layers.size() - 1).getChildPerNeuronNumber();
            if (childrenPerParentLayerNeuron > layer.getNeuronNumber()) {
                throw new NeuralNetworkGenerationException(
                        String.format("Layer %d: Each previous layer neuron has %d children,"
                        + " but there are only %d neurons in the layer to add",
                        layers.size(), childrenPerParentLayerNeuron, layer.getNeuronNumber()));
            }
        }
        layers.add(layer);
    }

    /**
     * @return layers of network
     */
    public List<Layer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

}
