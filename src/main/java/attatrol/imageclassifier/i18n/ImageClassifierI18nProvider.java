package attatrol.imageclassifier.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides text strings from resource bundles with intent of i18n of the
 * application.
 * 
 * @author attatrol
 *
 */
public final class ImageClassifierI18nProvider {

    /*
     * Current bundle.
     */
    private static ResourceBundle sCurrentBundle;

    /*
     * Utility class ctor, use <i>getText</i>.
     */
    private ImageClassifierI18nProvider() {

    }

    /**
     * Returns bundle value for a key. Also contains logics for bundle
     * searching.
     * 
     * @param key
     *            key.
     * @return bundle string value.
     */
    public static String getText(String key) {
        if (sCurrentBundle == null) {
            sCurrentBundle = ResourceBundle.getBundle("imageclassifier");
        }
        return sCurrentBundle.getString(key);
    }

    /**
     * Returns bundle value for a key as plain integer. Also contains logics for
     * bundle searching.
     * 
     * @param key
     *            key.
     * @return bundle string value as integer.
     */
    public static int getInt(String key) throws MissingResourceException {
        if (sCurrentBundle == null) {
            sCurrentBundle = ResourceBundle.getBundle("imageclassifier");
        }
        return Integer.parseInt(sCurrentBundle.getString(key));
    }

    /**
     * Returns bundle value for a key as double. Also contains logics for bundle
     * searching.
     * 
     * @param key
     *            key.
     * @return bundle string value as integer.
     */
    public static double getDouble(String key) throws MissingResourceException {
        if (sCurrentBundle == null) {
            sCurrentBundle = ResourceBundle.getBundle("imageclassifier");
        }
        return Double.parseDouble(sCurrentBundle.getString(key));
    }

}
