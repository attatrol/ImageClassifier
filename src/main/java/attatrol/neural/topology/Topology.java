package attatrol.neural.topology;

import java.util.Arrays;

/**
 * POJO, describes topology of the graph of some neural network.
 *
 * @author attatrol
 *
 */
public class Topology {

    /**
     * Used in {@link #toString()}. Expected that number of neurons wouldn't exceed 9999.
     */
    protected final static int INTEGER_PADDING = 4;

    /**
     * Title used in {@link #toString()}
     */
    protected final static String TOPOLOGY_TITLE = " Ordinary topology of neural network:";

    /**
     * Total number of sources (neurons and coordinates of incoming vector)
     */
    protected final int sourceTotalNumber;

    /**
     * Index of the first neuron. (First among sources are those of incoming vector,
     * neurons are after them).
     */
    protected final int neuronFirstIndex;

    /**
     * Index of the first surface neuron. Neurons that give values to the result vector are called 
     * "surface", and they should be located in the end of the sources array.
     */
    protected final int surfaceNeuronFirstIndex;

    /**
     * Sources give their output to their children, who are listed in this arrays
     */
    protected final int[][] sourceChildren;

    /**
     * Sources recieve their input values from other sources who are parents for them.
     * Note that input vector coordinates have zero parents.
     */
    protected final int[][] sourceParents;

    /**
     * Defines order in which analytical processor will traverse neurons
     */
    protected final int[] forwardTraverseNeuronsOrder;

    public Topology(int sourceTotalNumber, int neuronFirstIndex, int surfaceNeuronFirstIndex,
            int[][] sourceChildren, int[][] sourceParents, int[] forwardTraverseNeuronsOrder) {
        super();
        this.sourceTotalNumber = sourceTotalNumber;
        this.neuronFirstIndex = neuronFirstIndex;
        this.surfaceNeuronFirstIndex = surfaceNeuronFirstIndex;
        this.sourceChildren = sourceChildren;
        this.sourceParents = sourceParents;
        this.forwardTraverseNeuronsOrder = forwardTraverseNeuronsOrder;
    }

    public int getSourceTotalNumber() {
        return sourceTotalNumber;
    }

    public int getNeuronFirstIndex() {
        return neuronFirstIndex;
    }

    public int getSurfaceNeuronFirstIndex() {
        return surfaceNeuronFirstIndex;
    }

    public int[] getSourceChildrenByIndex(int neuronIndex) {
        return sourceChildren[neuronIndex];
    }

    public int[] getSourceParentsByIndex(int neuronIndex) {
        return sourceParents[neuronIndex];
    }

    public int[][] getSourceChildren() {
        return sourceChildren;
    }

    public int[][] getSourceParents() {
        return sourceParents;
    }

    public int[] getForwardTraverseNeuronsOrder() {
        return forwardTraverseNeuronsOrder;
    }

    /**
     * Sources are printed in order of forward traverse.
     * Incoming vector is printed first, followed by neurons,
     * typically surface neurons are printed last.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        final String nameOfChildrenColumn = "Children";
        int childrenColumnWidth = getSubarrayMaximumStringLength(sourceChildren);
        childrenColumnWidth = childrenColumnWidth > nameOfChildrenColumn.length()
                ? childrenColumnWidth : nameOfChildrenColumn.length();

        final String nameOfParentColumn = "Parents";
        int parentColumnWidth = getSubarrayMaximumStringLength(sourceParents);
        parentColumnWidth = parentColumnWidth > nameOfParentColumn.length()
                ? parentColumnWidth : nameOfParentColumn.length();

        final String header = String.format("| ## | Type | #P | #C | %-" + (parentColumnWidth - 1)
                + "s| %-" + (childrenColumnWidth - 1) + "s|", nameOfParentColumn, nameOfChildrenColumn);
        final String divider = getDividerString(header.length());

        sb.append(TOPOLOGY_TITLE).append(System.lineSeparator())
        .append(divider).append(System.lineSeparator())
        .append(header).append(System.lineSeparator())
        .append(divider).append(System.lineSeparator());

        final String rowFormat = "|%-" + INTEGER_PADDING + "d|%-6s|%-"
                + INTEGER_PADDING + "d|%-"
                + INTEGER_PADDING + "d|%-"
                + parentColumnWidth+ "s|%-"
                + childrenColumnWidth + "s|";
        for(int i = 0; i <sourceTotalNumber; i++) {
            final int currentIndex = i < neuronFirstIndex ? i : forwardTraverseNeuronsOrder[i - neuronFirstIndex];
            final String sourceType = getType(currentIndex);
            sb.append(String.format(rowFormat, currentIndex, sourceType,
                    sourceParents[currentIndex].length, sourceChildren[currentIndex].length,
                    arrayToString(sourceParents[currentIndex]), arrayToString(sourceChildren[currentIndex])))
                .append(System.lineSeparator());
        }

        return sb.append(divider).append(System.lineSeparator()).toString();
    }

    /**
     * Calculates longest possible string presentation of an integer subarrays in some array.
     * Used only in {@link #toString()}.
     * @param someArray some array
     * @param largestPossibleInteger integer to determine possible size of 
     * @return size of its longest subarray
     */
    protected static int getSubarrayMaximumStringLength(int[][] someArray) {
        int width = 0;
        for (int[] subarray : someArray) {
            int stringLength = 2; // brackets length
            if (subarray != null) {
                stringLength += subarray.length * (1 + INTEGER_PADDING);
            }
            else {
                stringLength += 4; // "null".length();
            }
            if (stringLength > width) {
                width = stringLength;
            }
        }
        return width;
    }

    /**
     * Creates a divider string of defined length.
     * Used in {@link #toString()} only.
     * @param dividerStringLength length of divider string.
     * @return divider string.
     */
    protected static String getDividerString(int dividerStringLength) {
        char[] charArray = new char[dividerStringLength];
        Arrays.fill(charArray, '-');
        String divider = new String(charArray);
        return divider;
    }

    /**
     * Produces fine string presentation of an integer array.
     * @param array integer array
     * @return array as string
     */
    protected static String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (array != null) {
            for (int i = 0; i < array.length - 1; i++) {
                sb.append(String.format("%-" + INTEGER_PADDING + "d", array[i])).append(" ");
            }
            if (array.length != 0) {
                sb.append(String.format("%-" + INTEGER_PADDING + "d", array[array.length - 1]));
            }
        }
        else {
            sb.append("null");
        }
        return sb.append(']').toString();
    }

    /**
     * Specifies type of current source.
     * @param currentIndex index of the source
     * @return type of the source as string.
     */
    protected String getType(int currentIndex) {
        if (currentIndex < neuronFirstIndex) {
            return "IN";
        }
        else if (currentIndex < surfaceNeuronFirstIndex) {
            return "HIDDEN";
        }
        else {
            return "OUT";
        }
    }

}
