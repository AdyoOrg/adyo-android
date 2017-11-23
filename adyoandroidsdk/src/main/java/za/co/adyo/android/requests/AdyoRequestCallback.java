package za.co.adyo.android.requests;

/**
 * AdyoRequestCallback
 * Request callback class used internally by the Adyo request classes
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/10/17
 */
public interface AdyoRequestCallback {
    void onComplete(AdyoRequestResponse adyoRequestResponse);

    void onFailed(AdyoRequestResponse adyoRequestResponse);
}
