package attatrol.neural.network;

import java.io.Serializable;

/**
 * POJO class, holds internal state of neural network.
 * It has 2 purposes:
 * <p>
 * 1. It is used as a transfer object from neural network to
 * learning and analytical processors. Such object are created by
 * shallow copying of network internal state and has flag {@link #isShallowCopy} = true.
 * <p>
 * 2. It is used for testing purposes, for marshalling, as a log record ets. If so,
 * it can be created by {@link NeuralNetwork#getNetworkStateCopy()} as a deep copy.
 * Flag {@link #isShallowCopy} = false.
 * <p>
 * There is nothing bad to transfer shallow copies somewhere else, while you are
 * in synchronized code and you know what are you doing, so flag is for
 * convenience only.
 * @author attatrol
 *
 */
public class NeuralNetworkState implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3829019744306394773L;

    private final transient boolean isShallowCopy;

    /**
     * Amplitude of input vector coordinates
     */
    private double inputVectorAmplitude;

    /**
     * Input vector size
     */
    private int inputVectorSize;

    /**
     * Result vector size
     */
    private int resultVectorSize;

    /*
     * Below are mutable variables of internal state of the neural network
     */

    /**
     * Weights for each neuron (there are null arrays placed on incoming vector indexes)
     */
    private float[][] weight;

    /**
     * Biases for each neuron
     */
    private float[] bias;

    /**
     * Linear combination of each neuron
     */
    private double[] linearCombination;

    /**
     * Outcoming values of each source (sources are neurons and input vector coordinates).
     * (Actually this is a result of some transmutation of linear combination, e.g. activation function) 
     */
    private double[] result;

    /**
     * Children of each source
     */
    private int[][] children;

    /**
     * Parents of each source
     */
    private int[][] parents;

    /**
     * Order of traverse for neurons.
     */
    private int[] traverseOrder;

    public NeuralNetworkState(boolean isShallowCopy, double inputVectorAmplitude,
            int inputVectorSize, int resultVectorSize, float[][] weight, float[] bias,
            double[] linearCombination, double[] result, int[][] children, int[][] parents,
            int[] traverseOrder) {
        super();
        this.isShallowCopy = isShallowCopy;
        this.inputVectorAmplitude = inputVectorAmplitude;
        this.inputVectorSize = inputVectorSize;
        this.resultVectorSize = resultVectorSize;
        this.weight = weight;
        this.bias = bias;
        this.linearCombination = linearCombination;
        this.result = result;
        this.children = children;
        this.parents = parents;
        this.traverseOrder = traverseOrder;
    }

    public double getInputVectorAmplitude() {
        return inputVectorAmplitude;
    }

    public void setInputVectorAmplitude(double inputVectorAmplitude) {
        this.inputVectorAmplitude = inputVectorAmplitude;
    }

    public int getInputVectorSize() {
        return inputVectorSize;
    }

    public void setInputVectorSize(int inputVectorSize) {
        this.inputVectorSize = inputVectorSize;
    }

    public int getResultVectorSize() {
        return resultVectorSize;
    }

    public void setResultVectorSize(int resultVectorSize) {
        this.resultVectorSize = resultVectorSize;
    }

    public float[][] getWeight() {
        return weight;
    }

    public void setWeight(float[][] weight) {
        this.weight = weight;
    }

    public float[] getBias() {
        return bias;
    }

    public void setBias(float[] bias) {
        this.bias = bias;
    }

    public double[] getLinearCombination() {
        return linearCombination;
    }

    public void setLinearCombination(double[] linearCombination) {
        this.linearCombination = linearCombination;
    }

    public double[] getResult() {
        return result;
    }

    public void setResult(double[] result) {
        this.result = result;
    }

    public int[][] getChildren() {
        return children;
    }

    public void setChildren(int[][] children) {
        this.children = children;
    }

    public int[][] getParents() {
        return parents;
    }

    public void setParents(int[][] parents) {
        this.parents = parents;
    }

    public int[] getTraverseOrder() {
        return traverseOrder;
    }

    public void setTraverseOrder(int[] traverseOrder) {
        this.traverseOrder = traverseOrder;
    }

    public boolean isShallowCopy() {
        return isShallowCopy;
    }

}
