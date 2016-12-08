package attatrol.neural;

/**
 * Thrown if wrong setting is encountered during network generation.
 * @author attatrol
 *
 */
public class NeuralNetworkGenerationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 9157182995980431679L;

    public NeuralNetworkGenerationException(String message) {
        super(message);
    }

}
