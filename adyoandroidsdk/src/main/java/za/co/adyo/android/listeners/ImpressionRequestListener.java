package za.co.adyo.android.listeners;

/**
 * ImpressionRequestListener
 * Request callback for recording impression requests
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/13/17
 */
public interface ImpressionRequestListener {
    
    void onRequestComplete(Integer impressionCode, Integer thirdPartyCode);

    void onRequestError(String impressionError, String thirdPartyImpressionError);
}
