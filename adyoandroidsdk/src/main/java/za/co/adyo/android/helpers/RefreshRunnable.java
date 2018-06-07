package za.co.adyo.android.helpers;

import android.content.Context;
import android.os.Handler;
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
    private boolean isPaused;
    private Handler handler;
    private boolean continueThread = true;

    public RefreshRunnable(Context context, PlacementRequestParams params, final PlacementRequestListener listener, boolean isPaused)
    {
        this.params = params;
        this.listener = listener;
        this.context = context;
        this.handler = new Handler();
        this.isPaused = isPaused;

    }

    @Override
    public void run() {



    }


    public void updatePausedStatus(boolean isPaused)
    {
        this.isPaused = isPaused;
    }

    public void updateContinueThread(boolean continueThread)
    {
        this.continueThread = continueThread;
    }


}
