package za.co.adyo.android.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import za.co.adyo.android.listeners.PlacementRequestListener;
import za.co.adyo.android.models.Placement;
import za.co.adyo.android.requests.PlacementRequestParams;

/**
 * AdyoWebViewClient
 * A custom WebViewClient with a placement property
 *
 * @author UnitX, marilie
 * @version 1.0, 11/16/17
 */

public abstract class AdyoWebViewClient extends WebViewClient {

    protected Placement placement;
    private Context context;
    protected PlacementRequestParams params;
    protected PlacementRequestListener listener;


    public AdyoWebViewClient(Context context) {
        this.context = context;
    }

    public void setPlacement(Placement placement, PlacementRequestParams params, PlacementRequestListener listener) {
        this.placement = placement;
        this.params = params;
        this.listener = listener;
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!placement.getCreativeUrl().equals(url)) {

            if(placement.getAppTarget() == Placement.APP_TARGET_DEFAULT) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
            return true;
        }
        return false;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url=request.getUrl().toString();

        if ((placement.getCreativeUrl() != null && !placement.getCreativeUrl().equals(url)) ||
                (placement.getCreativeHtml() != null && !placement.getCreativeHtml().equals(url))) {

            if(placement.getAppTarget() == Placement.APP_TARGET_DEFAULT) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
            return true;
        }
        return false;
    }

}