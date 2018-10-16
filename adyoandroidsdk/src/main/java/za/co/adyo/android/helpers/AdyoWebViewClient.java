package za.co.adyo.android.helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.customtabs.CustomTabsIntent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
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



//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//        String url=request.getUrl().toString();
//
//        if ((placement.getCreativeUrl() != null && !placement.getCreativeUrl().equals(url)) ||
//                (placement.getCreativeHtml() != null && !placement.getCreativeHtml().equals(url))) {
//
//            if(placement.getAppTarget() == Placement.APP_TARGET_DEFAULT) {
//
//                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                CustomTabsIntent customTabsIntent = builder.build();
//                customTabsIntent.launchUrl(context, Uri.parse(url));
//            }
//            return true;
//        }
//        return false;
//    }

}