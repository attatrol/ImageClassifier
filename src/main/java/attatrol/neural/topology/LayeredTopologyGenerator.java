package attatrol.neural.topology;

import java.util.Arrays;
import java.util.List;

import attatrol.neural.NeuralNetworkGenerationException;

/**
 * Utility class, contains methods of processing layered topology description into layered topology.
 */
public final class LayeredTopologyGenerator {

    /**
     * Utility class ctor.
     */
    private LayeredTopologyGenerator() {
    }

    /**
     * Generates layered topology from layered topology description.
     * @param layeredTopologyDescription the description
     * @return the topology
     * @throws NeuralNetworkGenerationException on failure to produce the topology. Generally no
     * layered 
     */
    public static LayeredTopology generate(LayeredTopologyDescription layeredTopologyDescription) 
            throws NeuralNetworkGenerationException {
        List<Layer> layers = layeredTopologyDescription.getLayers();
        //get cardinal bounds of the topology
        int sourceTotalNumber = 0;
        final int incomingTotalNumber = layers.get(0).getNeuronNumber();
        final int outcomingTotalNumber = layers.get(layers.size() - 1).getNeuronNumber();
        int[] firstIndexOfLayer = new int[layers.size()];
        for (int i = 0; i < layers.size(); i++) {
            firstIndexOfLayer[i] = sourceTotalNumber;
            sourceTotalNumber += layers.get(i).getNeuronNumber();
        }
        final int neuronFirstIndex = incomingTotalNumber;
        int surfaceNeuronFirstIndex = sourceTotalNumber - outcomingTotalNumber;
        int[][] sourceChildren = new int[sourceTotalNumber][];

        //iterative generation of layer forward interconnections
        int parentLevelOffset = 0;
        for (int i = 0; i < layers.size() - 1; i++) {
            final Layer parentLayer = layers.get(i);
            final Layer childLayer = layers.get(i + 1);
            final int childLayerStartIndex = parentLevelOffset  + parentLayer.getNeuronNumber();
            final int childLayerEndIndex = childLayerStartIndex + childLayer.getNeuronNumber() - 1;
            final int[][] offsetConnections = parentLayer.getDistribution()
                    .generateConnections(parentLayer.getNeuronNumber(),
                            childLayerStartIndex, childLayerEndIndex, parentLayer.getChildPerNeuronNumber());
            for (int j = 0; j < offsetConnections.length; j++) {
                sourceChildren[j + parentLevelOffset] = offsetConnections[j];
            }
            parentLevelOffset += parentLayer.getNeuronNumber();
        }
        //fill surface level with empty arrays
        for (int i = parentLevelOffset; i < sourceChildren.length; i++) {
            sourceChildren[i] = new int[0];
        }

        //generation of source parents from source children
        final int[][] sourceParents = getParents(sourceChildren);

        //traverse order generation
        final int[] forwardTraverseNeuronsOrder =
                getForwardTraverseOrder(sourceParents, sourceChildren, neuronFirstIndex);

        return new LayeredTopology(sourceTotalNumber, neuronFirstIndex,
                surfaceNeuronFirstIndex, sourceChildren, sourceParents, forwardTraverseNeuronsOrder,
                firstIndexOfLayer);
    }

    /**
     * Generates parents of sources from children of the sources.
     * @param sourceChildren children of the sources
     * @return parents of the sources
     */
    public static int[][] getParents(int[][] sourceChildren) {
        // get parent number per source
        int[] parentNumberPerSource = new int[sourceChildren.length];
        for (int[] children : sourceChildren) {
            for (int child : children) {
                parentNumberPerSource[child]++;
            }
        }

        // create proper return array
        int[][] sourceParents = new int[sourceChildren.length][];
        for (int i = 0; i < sourceChildren.length; i++) {
            sourceParents[i] = new int[parentNumberPerSource[i]];
        }

        // create arrays of indexes of first free parent cell in return array
        int[] currentIndex = new int[sourceChildren.length];

        // fill return array
        for (int i = 0; i < sourceChildren.length; i++) {
            for (int child : sourceChildren[i]) {
                sourceParents[child][currentIndex[child]] = i;
                currentIndex[child]++;
            }
        }
        for (int[] parents : sourceParents) {
            Arrays.sort(parents);
        }
        return sourceParents;
    }

    /**
     * Generate order of processing of each neuron by the neural network from the ordered graph
     * of the neural network. It is guaranteed that source values will be ready for the next neuron in
     * the sequence if all preceding are processed. You may reverse this order for backward processing of the
     * neurons (e.g. in backpropagation algorithm).
     * @param sourceParents parents of each source (sources = neurons + incoming vector)
     * @param sourceChildren children of each source
     * @param neuronFirstIndex first index of neuron among sources (sources are ordered: first vector values, after neurons)
     * @return proper order of processing
     * @throws NeuralNetworkGenerationException on failure to produce order of processing for all existing neurons,
     * this means either your graph isn't connected or there are cycles within it.
     */
    public static int[] getForwardTraverseOrder(int[][] sourceParents, int[][] sourceChildren, int neuronFirstIndex)
        throws NeuralNetworkGenerationException {
        int[] unreadyParentNumber = new int[sourceChildren.length];
        boolean[] alreadyTraversed = new boolean[sourceChildren.length];
        for (int i = 0; i < sourceChildren.length; i++) {
            unreadyParentNumber[i] = sourceParents[i].length;
        }

        int[] forwardTraverseOrder = new int[sourceChildren.length - neuronFirstIndex];
        int currentTraverceIndex = 0;
        int watchdog = 0;
        // main loop, we iterate over passages through array of neurons
        while (currentTraverceIndex < forwardTraverseOrder.length) {
            if (watchdog > forwardTraverseOrder.length + 1) {
                // watchdog == forwardTraverseOrder.length + 1 if and only if topology of the net is a simple chain
                throw new NeuralNetworkGenerationException("Topology is not connected, some neurons can not be reached");
            }
            // a passage through array of neurons
            for (int i = 0; i < sourceChildren.length; i++) {
                if (unreadyParentNumber[i] == 0 && !alreadyTraversed[i]) {
                    alreadyTraversed[i] = true;
                    if (i >= neuronFirstIndex) {
                        forwardTraverseOrder[currentTraverceIndex] = i;
                        currentTraverceIndex++;
                    }
                    for (int child : sourceChildren[i]) {
                        unreadyParentNumber[child]--;
                    }
                }
            }
            watchdog++;
        }
        return forwardTraverseOrder;
    }
}
