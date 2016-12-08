package attatrol.neural.network;

import java.util.Arrays;

import attatrol.neural.NeuralNetworkRuntimeException;
import attatrol.neural.analysis.AnalyticalProcessor;
import attatrol.neural.learning.LearningProcessor;
import attatrol.neural.topology.Topology;
import attatrol.neural.utils.RandomUtils;
import attatrol.neural.utils.Utils;

/**
 * This class describes a generic feed-forward neural network.
 * <p>
 * I plan to use this class as a base for many other types of neural networks.
 * However for additional flexibility analytical and learning processors are also allowed to
 * be mutable, thus one may add additional behavior not directly to this class, but via 
 * enhanced learners and analyzers.
 * 
 * @author attatrol
 *
 */
public class NeuralNetwork {

    /*
     * Below are initial immutable parameters of the neural network
     */

    /**
     * Lower bound for initial values of weights
     */
    public static final float RAMDOM_WEIGHT_MIN = -0.5f;

    /**
     * Upper bounds for initial values of weights
     */
    public static final float RANDOM_WEIGHT_MAX = 0.5f;

    /**
     * Settings used to generate this perceptron
     */
    protected NeuralNetworkSettings settings;

    /**
     * Topology of the neural network graph
     */
    protected Topology topology;

    /**
     * Analythical processor. Generates new result values for each neuron.
     */
    protected AnalyticalProcessor analyticalProcessor;

    /**
     * Learning processor. Tries to converge neural network to some ideal function.
     */
    protected LearningProcessor learningProcessor;

    /**
     * Amplitude of input vector coordinates
     */
    protected double inputVectorAmplitude;

    /**
     * Input vector size
     */
    protected int inputVectorSize;

    /**
     * Result vector size
     */
    protected int resultVectorSize;

    /*
     * Below are mutable variables of internal state of the neural network
     */

    /**
     * Weights for each neuron (there are null arrays placed on incoming vector indexes)
     */
    protected float[][] weight;

    /**
     * Biases for each neuron
     */
    protected float[] bias;

    /**
     * Linear combination of each neuron
     */
    protected double[] linearCombination;

    /**
     * Outcoming values of each source (sources are neurons and input vector coordinates).
     * (Actually this is a result of some transmutation of linear combination, e.g. activation function) 
     */
    protected double[] result;

    /**
     * Children of each source
     */
    protected int[][] children;

    /**
     * Parents of each source
     */
    protected int[][] parents;

    /**
     * Order of traverse for neurons.
     */
    protected int[] traverseOrder;

    /**
     * Only constructor for neural network.
     * Intended to be used by {@link NeuralNetworkFactory#provide(NeuralNetworkSettings)} only
     * @param settings 
     * @param topology
     */
    NeuralNetwork(NeuralNetworkSettings settings, Topology topology) {
        this.settings = settings;
        this.topology = topology;
        this.analyticalProcessor = settings.getAnalythicalProcessor();
        this.learningProcessor = settings.getLearningProcessor();
        this.inputVectorAmplitude = settings.getInputVectorAmplitude();
        this.resultVectorSize = settings.getResultVectorSize();
        this.inputVectorSize = settings.getInputVectorSize();

        // get deep copy of topology internal state
        this.traverseOrder = Utils.getCopy(topology.getForwardTraverseNeuronsOrder());
        this.children = Utils.getDeepCopy(topology.getSourceChildren());
        this.parents = Utils.getDeepCopy(topology.getSourceParents());

        // generate arrays with neurons states
        final int numberOfSources = parents.length;
        //initial values of biases cover medium zone of input vector domain
        this.bias = RandomUtils.generateRandomFloats(
                inputVectorAmplitude / 4, inputVectorAmplitude * 3 / 4, numberOfSources);
        this.linearCombination = new double[numberOfSources];
        this.result = new double[numberOfSources];
        this.weight = new float[numberOfSources][];
        for (int i = 0; i < numberOfSources; i++) {
            weight[i] = RandomUtils.generateRandomFloats(
                    RAMDOM_WEIGHT_MIN, RANDOM_WEIGHT_MAX, parents[i].length);
        }

    }

    /**
     * Neural network maps input vector into some result vector. 
     * @param inputVector argument
     * @return result vector
     * @throws NeuralNetworkRuntimeException on bad format of input vector or on internal failure
     * of analytical engine
     */
    public synchronized double[] map(double[] inputVector) throws NeuralNetworkRuntimeException {
        checkInputVector(inputVector);
        // put input vector values into sources' results
        for (int i = 0; i < inputVectorSize; i++) {
            result[i] = inputVector[i];
        }
        NeuralNetworkState shallowLiveState = getNetworkStateShallowCopy();
        analyticalProcessor.process(shallowLiveState, learningProcessor);
        replaceNetworkState(shallowLiveState);
        return Arrays.copyOfRange(result, result.length - resultVectorSize, result.length);
    }

    /**
     * Neural network will perform {@link #map(double[])},
     * then it will try to perform single learning iteration.
     * If you use iterative learning process, you should iterate over
     * your reference set with this method.
     * @param inputVector input vector
     * @param reference reference result vector, not used if learning is unsupervised
     * @return result vector
     * @throws NeuralNetworkRuntimeException on internal failure
     */
    public synchronized double[] learn(double[] inputVector, double[] reference) throws NeuralNetworkRuntimeException {
        final double[] answer = map(inputVector);
        NeuralNetworkState shallowLiveState = getNetworkStateShallowCopy();
        learningProcessor.process(shallowLiveState, reference, analyticalProcessor);
        replaceNetworkState(shallowLiveState);
        return answer;
        
    }

    /**
     * Creates deep copy of internal network state.
     * @return deep copy of network state
     */
    public synchronized NeuralNetworkState getNetworkStateCopy() {
        return new NeuralNetworkState(false,
                inputVectorAmplitude, inputVectorSize, resultVectorSize, 
                Utils.getDeepCopy(weight), 
                Utils.getCopy(bias), Utils.getCopy(linearCombination), Utils.getCopy(result),
                Utils.getDeepCopy(children), Utils.getDeepCopy(parents), Utils.getCopy(traverseOrder));
    }

    /**
     * Checks if input vector is valid.
     * @param inputVector input vector
     * @throws NeuralNetworkRuntimeException on invalid input vector
     */
    private void checkInputVector(double[] inputVector) throws NeuralNetworkRuntimeException {
        if (inputVector.length != inputVectorSize) {
            throw new NeuralNetworkRuntimeException(
                    String.format("Incoming vector has cardinality of %d, network accepts only %d",
                            inputVector.length, inputVectorSize));
        }
        for (int i = 0; i < inputVector.length; i++) {
            if (inputVector[i] < 0. || inputVector[i] > inputVectorAmplitude) {
                throw new NeuralNetworkRuntimeException(
                        String.format("Input vector coordinate %d has value of %f, which is out of bounds [0, %f]", 
                               i, inputVector[i], inputVectorAmplitude));
            }
        }
    }

    /**
     * Creates shallow copy of this neural network state.
     * @return
     */
    private NeuralNetworkState getNetworkStateShallowCopy() {
        return new NeuralNetworkState(true,
                inputVectorAmplitude, inputVectorSize, resultVectorSize, 
                weight, bias, linearCombination, result, children, parents, traverseOrder);
    }

    /**
     * Replaces internal state of the network with input state.
     * @param state input state.
     */
    private void replaceNetworkState(NeuralNetworkState state) {
        this.inputVectorAmplitude = state.getInputVectorAmplitude();
        this.resultVectorSize = state.getResultVectorSize();
        this.inputVectorSize = state.getInputVectorSize();
        this.weight = state.getWeight();
        this.bias = state.getBias();
        this.linearCombination = state.getLinearCombination();
        this.result = state.getResult();
        this.children = state.getChildren();
        this.parents = state.getParents();
        this.traverseOrder = state.getTraverseOrder();
     }

}

