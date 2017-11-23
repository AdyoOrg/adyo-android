package za.co.adyo.android.helpers;

import android.webkit.WebViewClient;

import za.co.adyo.android.models.Placement;

/**
 * AdyoWebViewClient
 * A custom WebViewClient with a placement property
 *
 * @author UnitX, marilie
 * @version 1.0, 11/16/17
 */

public abstract class AdyoWebViewClient extends WebViewClient {

    protected Placement placement;


    public AdyoWebViewClient() {

    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

}