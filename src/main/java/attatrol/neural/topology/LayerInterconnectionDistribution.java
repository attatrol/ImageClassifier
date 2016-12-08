package attatrol.neural.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import attatrol.neural.NeuralNetworkGenerationException;

/**
 * Describes how two neighbour layers of neural network
 * can be connected.
 * @author attatrol
 *
 */
public enum LayerInterconnectionDistribution {

    /**
     * Each layer of neurons presented as a set of continuous indexes,
     * e.g. {N, N+1, ..., M}. Take segment [N, M+1) from number axis and circle it.
     * Children of each parent neuron form continuous segment on the circle of child layer,
     * and all such segments are uniformly distributed on the circle.
     * I often use it to divide higher layers of neural network into separate neural networks
     * which share common lower layers.
     * <p>
     * This is the fastest of all three algorithms, use it for a full layer interconnection.
     */
    LOCALIZED("Each layer of neurons presented as a set of continuous indexes, "
            + "e.g. {N, N+1, ..., M}. Take segment [N, M+1) from number axis and circle it. "
            + "Children of each parent neuron form continuous segment on the circle of child layer, "
            + "and all such segments are uniformly distributed on the circle. "
            + "I often use it to divide higher layers of neural network into separate neural networks "
            + "which share common lower layers. "
            + "This is the fastest of all three algorithms, use it for a full layer interconnection.") {
        /**
         * {@inheritDoc}
         */
        @Override
        protected int[][] innerGenerateConnections(int parentLayerSize, int childLayerFirstIndex,
                int childLayerLastIndex, int childPerNeuronNumber) {
            final int numberOfChildren = childLayerLastIndex - childLayerFirstIndex + 1;
            float[] arcCenterCoordinate = divideCircleInArcs(parentLayerSize);

            int[][] connections = new int[parentLayerSize][childPerNeuronNumber];
            for (int i = 0; i < parentLayerSize; i++) {
                final int firstChildIndex;
                //different calculation for odd and even childPerNeuronNumber
                if (childPerNeuronNumber % 2 != 0) {
                    firstChildIndex = Math.round(arcCenterCoordinate[i] * numberOfChildren)
                            - childPerNeuronNumber / 2;
                }
                else {
                    firstChildIndex = (int) (arcCenterCoordinate[i] * numberOfChildren)
                            - childPerNeuronNumber / 2 + 1;
                }
                for (int j = 0; j < childPerNeuronNumber; j++) {
                    connections[i][j] = getNormalized(firstChildIndex + j, numberOfChildren) + childLayerFirstIndex;
                }
            }
            return connections;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void testConnections(int[][] connections, int childLayerFirstIndex, int childLayerLastIndex) 
                throws NeuralNetworkGenerationException {
            testNoEqualConnections(connections);
            testUniformity(connections);
            testChildlessParents(connections);
            testContinuity(connections,childLayerFirstIndex, childLayerLastIndex);
        }
    },

    /**
     * Each layer of neurons presented as a set of continuous indexes,
     * e.g. {N, N+1, ..., M}. Take segment [N, M+1) from number axis and circle it.
     * Children of each parent neuron smeared over a circle uniformly, and all sets
     * of children from each parent are distributed uniformly, so number of parents per child
     * always is equal to floor(total_number_of_connections/number_of_children) or exceeds it by 1.
     * Use this distribution to get ordinary neural networks.
     */
    // TODO create discrete determined algorithm, a-b cutting maybe
    DISPERSE("Each layer of neurons presented as a set of continuous indexes, "
            + "e.g. {N, N+1, ..., M}. Take segment [N, M+1) from number axis and circle it. "
            + "Children of each parent neuron smeared over a circle uniformly, and all sets "
            + "of children from each parent are distributed uniformly, so number of parents per child"
            + "always is equal to floor(total_number_of_connections/number_of_children) or exceeds it by 1."
            + " Use this distribution to get ordinary neural networks.") {
        /**
         * {@inheritDoc}
         */
        @Override
        protected int[][] innerGenerateConnections(int parentLayerSize, int childLayerFirstIndex,
                int childLayerLastIndex, int childPerNeuronNumber) {
            // calculating necessary invariants
            final int numberOfChildren = childLayerLastIndex - childLayerFirstIndex + 1;
            /*int maxNumberOfParents = parentLayerSize * childPerNeuronNumber / numberOfChildren;
            if (maxNumberOfParents * numberOfChildren != parentLayerSize * childPerNeuronNumber) {
                maxNumberOfParents++;
            }
            int minimalRangeBetweenBrothers = numberOfChildren / childPerNeuronNumber;
            int maximalRangeBetweenBrothers = minimalRangeBetweenBrothers + 1;
            int maximalRangeCount = numberOfChildren - minimalRangeBetweenBrothers * childPerNeuronNumber;
            int minimalRangeCount = childPerNeuronNumber - maximalRangeCount; */
            // cost is equal to number of parents for a child neuron
            // cost can not exceed maxNumberOfParents
            /*final int[] costs = new int[numberOfChildren];
            final boolean[] minimizedCostDistribution = getMinimizedCostDistribution(
                    minimalRangeBetweenBrothers, maximalRangeBetweenBrothers,
                    minimalRangeCount, maximalRangeCount, costs, maxNumberOfParents);*/

            int[][] connections = new int[parentLayerSize][childPerNeuronNumber];
            for (int i = 0; i < parentLayerSize; i++) {
                for (int j = 0; j < childPerNeuronNumber; j++) {
                    int childIndex = (int) Math.round(((double) j * numberOfChildren) / childPerNeuronNumber
                            + ((double) i * numberOfChildren) / parentLayerSize);
                    final int newChild = getNormalized(childIndex, numberOfChildren);
                    boolean collision = false;
                    for (int k = 0; k < j; k++) {
                        if (newChild  + childLayerFirstIndex == connections[i][k]) {
                            collision = true;
                            break;
                        }
                    }
                    if (!collision) {
                        connections[i][j] = newChild + childLayerFirstIndex;
                    }
                    else {
                        connections[i][j] = newChild == numberOfChildren - 1
                                ? childLayerFirstIndex : newChild + childLayerFirstIndex + 1;
                    }
                }
            }
            return connections;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void testConnections(int[][] connections, int childLayerFirstIndex, int childLayerLastIndex) 
                throws NeuralNetworkGenerationException {
            testNoEqualConnections(connections);
            testUniformity(connections);
            testChildlessParents(connections);
            testDispersity(connections,childLayerFirstIndex, childLayerLastIndex);
        }
    },

    /**
     * Each parent has random children, but there is a guarantee that there is a
     * uniform distribution of parents per every child (difference between number
     * of parents between every 2 child neurons is 0 or 1). Work very slow on large numbers
     * because of birthdays paradox: it tries to add shuffled list of children  to connections and
     * checks each time if there are no equal children among already recorded children of a parent.
     * Obviously check fails often on large sets of children.
     */
    //TODO too slow - switch from Collection.shuffle to proper derangements generation
    RANDOM("Each parent has random children, but there is a guarantee that there is a "
            + "uniform distribution of parents per every child (difference between number"
            + " of parents between every 2 child neurons is 0 or 1). Work very slow on large numbers"
            + " because of birthdays paradox: it tries to add shuffled list of children  to connections and "
            + "checks each time if there are no equal children among already recorded children of a parent.") {
        @Override
        protected int[][] innerGenerateConnections(int parentLayerSize, int childLayerFirstIndex,
                int childLayerLastIndex, int childPerNeuronNumber) {
            final int numberOfChildren = childLayerLastIndex - childLayerFirstIndex + 1;

            List<Integer> children = new ArrayList<Integer>();
            for (int i = childLayerFirstIndex; i <= childLayerLastIndex; i++) {
                children.add(i);
            }

            int maxNumberOfParents = parentLayerSize * childPerNeuronNumber / numberOfChildren;
            if (maxNumberOfParents * numberOfChildren != parentLayerSize * childPerNeuronNumber) {
                maxNumberOfParents++;
            }
            int[][] childrenMappingBounds = calculateMappingBounds(numberOfChildren, maxNumberOfParents,
                    parentLayerSize, childPerNeuronNumber);

            int currentBoundedRegion = 0;
            int[][] connections = new int[parentLayerSize][childPerNeuronNumber];
            // main cycle, execution time not determined
            while (currentBoundedRegion < maxNumberOfParents) {
                // put shiffled children into the region of connections
                Collections.shuffle(children);
                int childIndex = 0;
                boolean currentCycleSucceed = true;
                // traverce over current bounds of mapping
                breakOnEqualChildrenOccurence:
                for (int i = childrenMappingBounds[currentBoundedRegion][0];
                        i <= childrenMappingBounds[currentBoundedRegion][2]; i++) {
                    for (int j = (i == childrenMappingBounds[currentBoundedRegion][0]
                            ? childrenMappingBounds[currentBoundedRegion][1] : 0);
                        j <= (i == childrenMappingBounds[currentBoundedRegion][2]
                                ? childrenMappingBounds[currentBoundedRegion][3]
                                : childPerNeuronNumber - 1); j++) {
                        final int currentChild = children.get(childIndex);
                        connections[i][j] = currentChild;
                        // check if this child is not present among already listed children of this parent
                        for (int k = 0; k < j; k++) {
                            if (currentChild == connections[i][k]) {
                                currentCycleSucceed = false;
                                break breakOnEqualChildrenOccurence;
                            }
                        }
                        childIndex++;
                    }
                }
                if (currentCycleSucceed) {
                    currentBoundedRegion++;
                }
            }
            return connections;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void testConnections(int[][] connections, int childLayerFirstIndex, int childLayerLastIndex) 
                throws NeuralNetworkGenerationException {
            testNoEqualConnections(connections);
            testUniformity(connections);
            testChildlessParents(connections);
        }

        /**
         * We are going to map shiffled children list into some regions on the connections double array
         * so this calculates borders of such regions in the connections double array
         * @param numberOfChildren number of neurons in child layer
         * @param maxNumberOfParents maximum number of parents per child neuron
         * @param parentLayerSize neuron number in parent layer
         * @param childPerNeuronNumber number of children per parent layer neuron
         * @return borders for each sequential child list mapping
         */
        private int[][] calculateMappingBounds(int numberOfChildren, int maxNumberOfParents,
                int parentLayerSize, int childPerNeuronNumber) {
            int[][] childrenMappingBounds = new int[maxNumberOfParents][4];
            int childSetCounter = 0;
            int childSetSizeCounter = 0;
            for (int i = 0; i < parentLayerSize; i++) {
                for (int j = 0; j < childPerNeuronNumber; j++) {
                    if (childSetSizeCounter == 1) {
                        childrenMappingBounds[childSetCounter - 1][2] = i;
                        childrenMappingBounds[childSetCounter - 1][3] = j;
                    }
                    if (childSetSizeCounter == 0) {
                        childSetSizeCounter = numberOfChildren;
                        childrenMappingBounds[childSetCounter][0] = i;
                        childrenMappingBounds[childSetCounter][1] = j;
                        childSetCounter++;
                    }
                    childSetSizeCounter--;
                }
            }
            childrenMappingBounds[maxNumberOfParents - 1][2] = parentLayerSize - 1;
            childrenMappingBounds[maxNumberOfParents - 1][3] = childPerNeuronNumber - 1;
            return childrenMappingBounds;
        }
    };

    /**
     * Generates connections between layers.
     * @param parentLayerSize number of neurons in parent layer
     * @param childLayerFirstIndex child layer first index (in global set of sources)
     * @param childLayerLastIndex child layer last index (in global set of sources)
     * @param childPerNeuronNumber number of connections from each parent neuron
     * @return connections between 2 layers of neurons
     * @throws NeuralNetworkGenerationException on failure to create proper connections
     */
    int[][] generateConnections(int parentLayerSize,
            int childLayerFirstIndex, int childLayerLastIndex, int childPerNeuronNumber)
        throws NeuralNetworkGenerationException {
        // preliminary checks
        if (childPerNeuronNumber > childLayerLastIndex - childLayerFirstIndex + 1) {
            throw new NeuralNetworkGenerationException("Parent layer neurons cant have more "
                    + "children per neuron than number of neurons in child layer");
        }
        if (childPerNeuronNumber * parentLayerSize <  childLayerLastIndex - childLayerFirstIndex + 1) {
            throw new NeuralNetworkGenerationException("Parent layer neurons cant have more "
                    + "children per neuron than number of neurons in child layer");
        }
        // generation
        int[][] result = innerGenerateConnections(parentLayerSize,
                childLayerFirstIndex, childLayerLastIndex, childPerNeuronNumber);
        // sorting
        for (int[] token : result) {
            Arrays.sort(token);
        }
        // testing (always test sorted arrays)
        testConnections(result, childLayerFirstIndex, childLayerLastIndex);
        return result;
    }

    /**
     * A detailed description of enum member used in ui.
     */
    private String description;

    /**
     * Default ctor.
     * @param description detailed description of enum member used in ui.
     */
    private LayerInterconnectionDistribution(String description) {
        this.description = description;
    }

    /**
     * @return detailed description of enum member used in ui
     */
    public String getDescription() {
        return description;
    }

    /**
     * Exact algorithm of connection generation realized by each type of
     * interlayer connection distribution.
     * @param parentLayerSize number of neurons in parent layer
     * @param childLayerFirstIndex child layer first index (in global set of sources)
     * @param childLayerLastIndex child layer last index (in global set of sources)
     * @param childPerNeuronNumber number of connections from each parent neuron
     * @return connections between 2 layers of neurons
     */
    abstract protected int[][] innerGenerateConnections(int parentLayerSize, int childLayerFirstIndex,
            int childLayerLastIndex, int childPerNeuronNumber);

    /**
     * Here some invariants of generated connections are tested.
     * Algorithms which generate connections rely on precision of float division
     * and on precision of {@link Math#round(float)}, they perform fine on small
     * sized layers but may fail on large layers. Failure to pass this test means
     * either inadequate precision of float arithmetics or general failure of algorithm.
     * @param connections sorted set of connections
     * @param childLayerLastIndex 
     * @param childLayerFirstIndex 
     * @throws NeuralNetworkGenerationException on failure to pass tests
     */
    protected abstract void testConnections(int[][] connections, int childLayerFirstIndex, int childLayerLastIndex) 
            throws NeuralNetworkGenerationException;

    /**
     * Finds true coordinate of point j on a integer circle with coordinates [0, numberOfChildren -1]
     * after j was subject of some mathematical operation.
     * @param j integer coordinate on circle to be normalized
     * @param numberOfChildren number of integer points on circle
     * @return nurmalized j
     */
    private static int getNormalized(int j, int numberOfChildren) {
        while (j < 0) {
            j += numberOfChildren;
        }
        while (j >= numberOfChildren) {
            j -= numberOfChildren;
        }
        return j;
    }

    /**
     * View child layer as a circle with symmetrically equally placed neurons on it,
     * each neuron's coordinate is a normalized neuron index
     * let us divide circle into several arcs
     * @param numberOfArcs number of arcs, presumed to be positive 
     * @return coordinates of the first points (or median points) of the arcs
     */
    private static float[] divideCircleInArcs(int numberOfArcs) {
        float arcStep = 1.f/numberOfArcs;
        float[] arcCenterCoordinate = new float[numberOfArcs];
        for (int i = 0; i < numberOfArcs; i++) {
            arcCenterCoordinate[i] = i * arcStep;
        }
        return arcCenterCoordinate;
    }



    /*
     * Specific tests for created connections placed below.
     */

    /**
     * Connections is equal if they have the same parent and child.
     * Checks if there no equal connections.
     * @param connections generated connections
     * @throws NeuralNetworkGenerationException on test failure
     */
    private static void testNoEqualConnections(int[][] connections)
            throws NeuralNetworkGenerationException {
        for (int[] children : connections) {
            Set<Integer> uniqueChildren = new HashSet<>();
            for (int child : children) {
                if (uniqueChildren.contains(child)) {
                    throw new NeuralNetworkGenerationException("Equal connection encountered");
                }
                else {
                    uniqueChildren.add(child);
                }
            }
        }
    }

    /**
     * There are N parent neurons, each having K children
     * and there is total of M child neurons.
     * Tests if each child has an equal number of parents 
     * ( floor(K*N/M) || floor(K*N/M) + 1).
     * @param connections generated connections
     * @throws NeuralNetworkGenerationException on test failure
     */
    private static void testUniformity(int[][] connections)
        throws NeuralNetworkGenerationException {
        Map<Integer, Integer> parentPerChild = new HashMap<>();
        for (int[] children : connections) {
            for (int child : children) {
                Integer numberOfParents = parentPerChild.get(child);
                if (numberOfParents == null) {
                    parentPerChild.put(child, 1);
                }
                else {
                    parentPerChild.put(child, numberOfParents++);
                }
            }
        }
        int parentNumber1 = -1;
        int parentNumber2 = -1;
        for (Integer parentNumber : parentPerChild.values()) {
            if (parentNumber1 < 0) {
                parentNumber1 = parentNumber;
            }
            else {
                if (parentNumber != parentNumber1) {
                    if (parentNumber2 < 0) {
                        parentNumber2 = parentNumber;
                    }
                    else if (parentNumber != parentNumber2) {
                        throw new NeuralNetworkGenerationException(
                                "Uniformity test found 3rd value for parents per child number: " + parentNumber);
                    }
                }
            }
        }
        if (parentNumber2 != -1 && Math.abs(parentNumber1 - parentNumber2) != 1) {
            throw new NeuralNetworkGenerationException(
                    "Uniformity test found that difference between values for parents per child number is greater than 1");
        }
    }

    /**
     * Checks if there are childless parents
     * @param connections generated connections
     * @throws NeuralNetworkGenerationException on test failure
     */
    private static void testChildlessParents(int[][] connections)
        throws NeuralNetworkGenerationException {
        for (int[] children : connections) {
            if (children.length == 0) {
                throw new NeuralNetworkGenerationException("Childless neuron encounered");
            }
        }
    }

    /**
     * Checks if children form continious integer set.
     * @param connections generated connections
     * @param childLayerFirstIndex child layer first index (in global set of sources)
     * @param childLayerLastIndex child layer last index (in global set of sources)
     * @throws NeuralNetworkGenerationException on test failure
     */
    private static void testContinuity(int[][] connections, int childLayerFirstIndex, int childLayerLastIndex)
            throws NeuralNetworkGenerationException {
        int numberOfChildren = childLayerLastIndex - childLayerFirstIndex + 1;
        for (int[] children : connections) {
            List<Integer> ranges = getRanges(children, numberOfChildren);
            for(Integer range : ranges) {
                boolean singleLongRangeEncountered = false;
                if (range != 1) {
                    if (!singleLongRangeEncountered) {
                        singleLongRangeEncountered = true;
                    }
                    else {
                        throw new NeuralNetworkGenerationException("Distance between neurons is not equal to 1. "
                                + "Set of children is not continious");
                    }
                }
            }
        }
    }

    /**
     * Checks if children are dispersed over circle well enough.
     * @param connections generated connections
     * @param childLayerFirstIndex child layer first index (in global set of sources)
     * @param childLayerLastIndex child layer last index (in global set of sources)
     * @throws NeuralNetworkGenerationException on test failure
     */
    private static void testDispersity(int[][] connections, int childLayerFirstIndex, int childLayerLastIndex)
            throws NeuralNetworkGenerationException {
        int numberOfChildren = childLayerLastIndex - childLayerFirstIndex + 1;
        for (int[] children : connections) {
            if (children.length > 1) {
                List<Integer> ranges = getRanges(children, numberOfChildren);
                int minimalDistance = numberOfChildren / children.length;
                for(Integer range : ranges) {
                    if (range != minimalDistance && range != (minimalDistance + 1)) {
                        throw new NeuralNetworkGenerationException(String.format("Distance between neurons is too short," +
                                " it equals %d, but should't be lesser than %d", range, minimalDistance));
                    }
                }
            }
        }
    }

    /**
     * Calculates ranges between neighbours in ordered point set on circle.
     * @param orderedPointSet points (childrens of some neuron)
     * @param curcleLength length of a curcle (ringed child layer of neurons)
     * @return
     */
    private static List<Integer> getRanges(int[] orderedPointSet, int circleLength) {
        List<Integer> ranges = new ArrayList<>();
        for (int i = 0; i < orderedPointSet.length - 1; i++) {
            int range1 = Math.abs(orderedPointSet[i] - orderedPointSet[i + 1]);
            int range2 = circleLength - range1;
            ranges.add(range1 < range2 ? range1 : range2);
        }
        int range1 = Math.abs(orderedPointSet[0] - orderedPointSet[orderedPointSet.length - 1]);
        int range2 = circleLength - range1;
        ranges.add(range1 < range2 ? range1 : range2);
        return ranges;
    }
}
