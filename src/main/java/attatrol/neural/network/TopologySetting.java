package attatrol.neural.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import attatrol.neural.NeuralNetworkGenerationException;
import attatrol.neural.topology.LayerInterconnectionDistribution;
import attatrol.neural.topology.Layer;
import attatrol.neural.topology.LayerType;
import attatrol.neural.topology.LayeredTopologyDescription;

/**
 * Describes what topology will be used within some neural network.
 * @author attatrol
 *
 */
public enum TopologySetting {
    /**
     * The very basic type of the perceptron, full interconnection between the only layer
     * of neurons and layer of input vector.
     */
    ONE_LAYER_PERCEPTRON(true, "The very basic type of the perceptron, full interconnection between the only layer "
            + "of neurons and layer of input vector.") {
        /**
         * {@inheritDoc}
         */
        @Override
        public LayeredTopologyDescription ltdFactory(
                NeuralNetworkSettings settings) throws NeuralNetworkGenerationException {
            LayeredTopologyDescription ltd = new LayeredTopologyDescription();
            ltd.addLayer(new Layer(settings.getInputVectorSize(), settings.getResultVectorSize(),
                        LayerInterconnectionDistribution.LOCALIZED, LayerType.INPUT_VECTOR));
            ltd.addLayer(new Layer(settings.getResultVectorSize(), 1,
                        LayerInterconnectionDistribution.LOCALIZED, LayerType.SURFACE));
            return ltd;
        }

        @Override
        public String toString() {
            return "One layer perceptron";
        }
    },
    /**
     * Perceptron with 2 layers of neurons, full interconnection between all neighbour layers.
     * Fist layer of neurons has the same size as input vector
     */
    TWO_LAYER_PERCEPTRON(true, "Perceptron with 2 layers of neurons,"
            + " full interconnection between all neighbour layers. "
            + "Fist layer of neurons has the same size as input vector") {
        /**
         * {@inheritDoc}
         */
        @Override
        public LayeredTopologyDescription ltdFactory(
                NeuralNetworkSettings settings) throws NeuralNetworkGenerationException {
            LayeredTopologyDescription ltd = new LayeredTopologyDescription();
            ltd.addLayer(new Layer(settings.getInputVectorSize(), settings.getInputVectorSize(),
                        LayerInterconnectionDistribution.LOCALIZED, LayerType.INPUT_VECTOR));
            ltd.addLayer(new Layer(settings.getInputVectorSize(), settings.getResultVectorSize(),
                    LayerInterconnectionDistribution.LOCALIZED, LayerType.ORDINARY));
            ltd.addLayer(new Layer(settings.getResultVectorSize(), 1,
                        LayerInterconnectionDistribution.LOCALIZED, LayerType.SURFACE));
            return ltd;
        }

        @Override
        public String toString() {
            return "Two layer perceptron";
        }
    },
    /**
     * Tree layered percepton, it has one hidden layer, full interconnection between layers.
     * Hidden layer size is an average between input and result vector sizes.
     * Neighbour layers are fully interconnected.
     */
    THREE_LAYER_PERCEPTRON(true, "Tree layered percepton, it has one hidden layer, full interconnection between layers."
            + " Hidden layer size is an average between input and result vector sizes. "
            + "Neighbour layers are fully interconnected.") {
        /**
         * {@inheritDoc}
         */
        @Override
        public LayeredTopologyDescription ltdFactory(
                NeuralNetworkSettings settings) throws NeuralNetworkGenerationException {
            return getClassic3LayerPerceptron(settings.getInputVectorSize(), settings.getResultVectorSize(),
                    (settings.getInputVectorSize() + settings.getResultVectorSize()) / 2);
        }

        @Override
        public String toString() {
            return "Tree layer perceptron";
        }
    },
    /**
     * Tree layered percepton, it has one hidden layer, full interconnection between layers.
     * Hidden layer size is a sum of sizes of first and surface layers.
     * Neighbour layers are fully interconnected.
     */
    WIDE_THREE_LAYER_PERCEPTRON(true, "Tree layered percepton, it has one hidden layer, full interconnection between layers."
            + " Hidden layer size is a sum of sizes of first and surface layers. Neighbour layers are fully interconnected.") {
        /**
         * {@inheritDoc}
         */
        @Override
        public LayeredTopologyDescription ltdFactory(
                NeuralNetworkSettings settings) throws NeuralNetworkGenerationException {
            return getClassic3LayerPerceptron(settings.getInputVectorSize(), settings.getResultVectorSize(),
                    settings.getInputVectorSize() + settings.getResultVectorSize());
        }

        @Override
        public String toString() {
            return "Wide three layer perceptron";
        }
    },
    /**
     * Layers form a pyramid, in each subsequent layer number of neurons is halved,
     * Full interconnection only between layer of input vector and first layer of neurons,
     * Neighbour layers are fully interconnected.
     * <p>
     * This is experimental topology.
     * <p>
     * First layer should be much wider than surface, otherwise pyramid height will be small.
     */
    PYRAMIDAL(true, "Layers form a pyramid, in each subsequent layer number of neurons is halved, "
            + "Full interconnection only between layer of input vector and first layer of neurons, "
            + "Neighbour layers are fully interconnected. This is experimental topology. "
            + "First layer should be much wider than surface, otherwise pyramid height will be small.") {
        /**
         * {@inheritDoc}
         */
        @Override
        public LayeredTopologyDescription ltdFactory(
                NeuralNetworkSettings settings) throws NeuralNetworkGenerationException {
            final List<Integer> layerSizes = getPyramidalLayerSizes(settings.getInputVectorSize(),
                    settings.getResultVectorSize(), 2);
            LayeredTopologyDescription ltd = new LayeredTopologyDescription();
            ltd.addLayer(new Layer(layerSizes.get(0), layerSizes.get(1),
                        LayerInterconnectionDistribution.LOCALIZED, LayerType.INPUT_VECTOR));
            if (layerSizes.size() > 2) {
                for (int i = 1; i < layerSizes.size() - 1; i++) {
                    ltd.addLayer(new Layer(layerSizes.get(i), layerSizes.get(i + 1),
                            LayerInterconnectionDistribution.LOCALIZED, LayerType.ORDINARY));
                }
            }
            ltd.addLayer(new Layer(layerSizes.get(layerSizes.size() - 1), 1,
                        LayerInterconnectionDistribution.LOCALIZED, LayerType.SURFACE));
            return ltd;
        }

        @Override
        public String toString() {
            return "Pyramidal";
        }
    },
    /**
     * Layers form a pyramid, in each subsequent layer number of neurons is halved,
     * Full interconnection only between layer of input vector and first layer of neurons,
     * In other layers a neuron is connected only to one half of neurons of the next layer, and its
     * children are grouped in continuous arrays of indexes.
     * <p>
     * This is experimental topology.
     * <p>
     * First layer should be much wider than surface, otherwise pyramid height will be small.
     */
    PYRAMIDAL_LOCALIZED(true, "Layers form a pyramid, in each subsequent layer number of neurons is halved, "
            + "Full interconnection only between layer of input vector and first layer of neurons, "
            + "In other layers a neuron is connected only to one half of neurons of the next layer, and its "
            + "children are grouped in continuous arrays of indexes.") {
        /**
         * {@inheritDoc}
         */
        @Override
        public LayeredTopologyDescription ltdFactory(
                NeuralNetworkSettings settings) throws NeuralNetworkGenerationException {
            final List<Integer> layerSizes = getPyramidalLayerSizes(settings.getInputVectorSize(),
                    settings.getResultVectorSize(), 2);
            LayeredTopologyDescription ltd = new LayeredTopologyDescription();
            ltd.addLayer(new Layer(layerSizes.get(0), calculateChildNumber(layerSizes.get(0), layerSizes.get(1)),
                        LayerInterconnectionDistribution.LOCALIZED, LayerType.INPUT_VECTOR));
            if (layerSizes.size() > 2) {
                for (int i = 1; i < layerSizes.size() - 1; i++) {
                    ltd.addLayer(new Layer(layerSizes.get(i),
                            calculateChildNumber(layerSizes.get(i), layerSizes.get(i + 1)),
                            LayerInterconnectionDistribution.LOCALIZED, LayerType.ORDINARY));
                }
            }
            ltd.addLayer(new Layer(layerSizes.get(layerSizes.size() - 1), 1,
                        LayerInterconnectionDistribution.LOCALIZED, LayerType.SURFACE));
            return ltd;
        }

        /**
         * Calculates number of children for a parent neuron.
         * Tries to make this number equal to childLayerSize/2 if possible.
         * @param parentLayerSize parent layer size
         * @param childLayerSize child layer size
         * @return number of children per parent layer neuron
         */
        private int calculateChildNumber(Integer parentLayerSize, Integer childLayerSize) {
            int result = childLayerSize / 2;
            if (result * parentLayerSize < childLayerSize) {
                result = childLayerSize / parentLayerSize;
                if (result * parentLayerSize < childLayerSize) {
                    result++;
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "Localized pyramidal";
        }
    },
    /**
     * Signals that topology is preset within settings
     */
    PRESET_TOPOLOGY(false, "Signals that topology is preset within settings"),

    /**
     * Signals that topology description is preset within settings
     */
    PRESET_TOPOLOGY_DESCRIPTION(false, "Signals that topology description is preset within settingsw") {

        @Override
        public String toString() {
            return "Preset topology description";
        }
    };

    /**
     * A list of ready for user topology settings
     */
    public static final List<TopologySetting> USER_OPTIONS;
    static {
        List<TopologySetting> userOptions = new ArrayList<>();
        for (TopologySetting type : TopologySetting.values()) {
            if (type.containsLTDFactory()) {
                userOptions.add(type);
            }
        }
        USER_OPTIONS = Collections.unmodifiableList(userOptions);
    }

    /**
     * True if a setting contains layered topology description factory.
     */
    private boolean containsLTDFactory;

    /**
     * Detailed description of current option
     */
    private String description;

    /**
     * Default ctor.
     * @param isUserOption
     */
    private TopologySetting(boolean isUserOption, String description) {
        this.containsLTDFactory = isUserOption;
        this.description = description;
    }

    /**
     * @return true if a setting contains layered topology description factory.
     */
    public boolean containsLTDFactory() {
        return containsLTDFactory;
    }

    /**
     * Generates layered topology description from network settings
     * @param settings network settings
     * @return layered topology description
     * @throws NeuralNetworkGenerationException if this topology setting does not contain ltd factory
     * or on generation error
     */
    public LayeredTopologyDescription generateTopologyDescription(
            NeuralNetworkSettings settings) throws NeuralNetworkGenerationException {
        if (containsLTDFactory) {
            return ltdFactory(settings);
        }
        else {
            throw new NeuralNetworkGenerationException(this.name()
                    + "has no internal layered topology description factory");
        }
    }

    /**
     * @return string description of a setting used by ui.
     */
    public String getDescription() {
        return description;
    }

    protected LayeredTopologyDescription ltdFactory(
            NeuralNetworkSettings settings) throws NeuralNetworkGenerationException {
        // should be overridden if entity contains ltd factory
        return null;
    }

    /**
     * Creates classic perceptron with a hidden layer.
     * First layer of neurons has size of input vector.
     * Second layer (hidden) has variable size.
     * Third surface layer has size of result vector.
     * @param inputVectorSize input vector size (first layer size)
     * @param resultVectorSize result vector size (third layer size)
     * @param hiddenLayerPerceptronSize hidden layer size (second layer size)
     * @return topology description of classic perceptron
     * @throws NeuralNetworkGenerationException on bad input numbers, e.g. negative ones
     */
    public static LayeredTopologyDescription getClassic3LayerPerceptron (
            int inputVectorSize, int resultVectorSize, int hiddenLayerSize) throws NeuralNetworkGenerationException {

        LayeredTopologyDescription ltd = new LayeredTopologyDescription();
        ltd.addLayer(new Layer(inputVectorSize, hiddenLayerSize,
                    LayerInterconnectionDistribution.LOCALIZED, LayerType.INPUT_VECTOR));
        ltd.addLayer(new Layer(hiddenLayerSize, resultVectorSize,
                LayerInterconnectionDistribution.LOCALIZED, LayerType.ORDINARY));
        ltd.addLayer(new Layer(resultVectorSize, 1,
                    LayerInterconnectionDistribution.LOCALIZED, LayerType.SURFACE));
        return ltd;
    }

    /**
     * Used to create pyramid layer structures.
     * @param firstLayerSize first layer size
     * @param surfaceLayerSize surface layer size
     * @param i divisor for each layer.
     * @return
     */
    private static List<Integer> getPyramidalLayerSizes(int firstLayerSize, int surfaceLayerSize, int i) {
        List<Integer> layerSizes = new ArrayList<>();
        int currentLayerSize = firstLayerSize;
        while (currentLayerSize > surfaceLayerSize) {
            layerSizes.add(currentLayerSize);
            currentLayerSize /= i;
        }
        layerSizes.add(surfaceLayerSize);
        return layerSizes;
    }

}
