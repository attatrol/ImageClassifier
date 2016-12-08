package attatrol.neural.ui.javafx.misc;

/**
 * Generic factory class for various objects
 * @author attatrol
 *
 * @param <T> class of generated object
 */
public interface AbstractUiFactory<T> {

    /**
     * Factory method, will return null if incoming parameters are invalid.
     * @param parameters list of incoming parameters
     * @return generated object
     */
    T generate(Object... parameters);

}
