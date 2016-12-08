package attatrol.neural.topology;

/**
 * Topology that also holds information
 * about layers of neural network
 *
 * @author attatrol
 *
 */
public class LayeredTopology extends Topology {

    /**
     * First index of the first neuron (or source) in every layer
     */
    private final int[] firstIndexOfLayer;

    public LayeredTopology(int sourceTotalNumber, int neuronFirstIndex, int surfaceNeuronFirstIndex,
            int[][] sourceChildren, int[][] sourceParents, int[] forwardTraverseNeuronsOrder,
            int[] firstIndexOfLayer) {
        super(sourceTotalNumber, neuronFirstIndex, surfaceNeuronFirstIndex, sourceChildren,
                sourceParents, forwardTraverseNeuronsOrder);
        this.firstIndexOfLayer = firstIndexOfLayer;
    }

    public int[] getFirstIndexOfLayer() {
        return firstIndexOfLayer;
    }

    /**
     * Specifies type of current source.
     * @param currentIndex index of the source
     * @return type of the source as string.
     */
    @Override
    protected String getType(int currentIndex) {
        if (currentIndex < neuronFirstIndex) {
            return "IN";
        }
        else if (currentIndex < surfaceNeuronFirstIndex) {
            String result = null;
            for(int i = 1; i < firstIndexOfLayer.length; i++) {
                if (firstIndexOfLayer[i] > currentIndex) {
                    result = "HL" + (i - 1);
                    break;
                }
            }
            if (result == null) {
                // if our network has single layer
                result = "HL1";
            }
            return result;
        }
        else {
            return "OUT";
        }
    }

}
