package za.co.adyo.android.helpers;

import android.content.Context;
import android.util.Log;

import za.co.adyo.android.listeners.PlacementRequestListener;
import za.co.adyo.android.requests.PlacementRequestParams;

/**
 * RefreshRunnable
 * A runnable to help with refreshing a placement
 *
 * @author UnitX, marilie
 * @version 1.0, 11/16/17
 */


public class RefreshRunnable implements Runnable {

    private PlacementRequestParams params;
    private PlacementRequestListener listener;
    private Context context;

    public RefreshRunnable(Context context, PlacementRequestParams params, final PlacementRequestListener listener)
    {
        this.params = params;
        this.listener = listener;
        this.context = context;

    }

    @Override
    public void run() {
        Log.d("ADYO", "Refreshing Placement");

        Adyo.requestPlacement(context, params, listener);
    }
}
