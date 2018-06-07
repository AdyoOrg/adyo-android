package za.co.adyo.android.helpers;

import android.content.Context;

import za.co.adyo.android.listeners.PlacementRequestListener;
import za.co.adyo.android.requests.PlacementRequestParams;

/**
 * RequestRunnable
 * A runnable to help with requesting a placement after the view has been rendered
 *
 * @author UnitX, marilie
 * @version 1.0, 11/16/17
 */


public class RequestRunnable implements Runnable {

    protected PlacementRequestParams params;
    protected PlacementRequestListener listener;
    private Context context;

    public RequestRunnable(Context context, PlacementRequestParams params, final PlacementRequestListener listener)
    {
        this.params = params;
        this.listener = listener;
        this.context = context;

    }

    @Override
    public void run() {



    }
}
