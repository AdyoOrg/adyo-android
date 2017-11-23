package za.co.adyo.android.listeners;

import za.co.adyo.android.models.Placement;

/**
 * PlacementRequestListener
 * Request callback for placement requests
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/13/17
 */
public interface PlacementRequestListener {
    
    void onRequestComplete(boolean isFound, Placement placement);

    void onRequestError(String error);
}
