package za.co.adyo.android.views;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import za.co.adyo.android.helpers.Adyo;
import za.co.adyo.android.helpers.AdyoWebViewClient;
import za.co.adyo.android.helpers.RequestRunnable;
import za.co.adyo.android.listeners.PlacementRequestListener;
import za.co.adyo.android.models.Placement;
import za.co.adyo.android.requests.PlacementRequestParams;

/**
 * AdyoZone
 * A custom view responsible for displaying an Adyo placement
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/10/17
 */


public class AdyoZoneView extends FrameLayout {

    private int width = 0;
    private int height = 0;
    private Context context;
    private Placement currentPlacement = null;
    private boolean isPaused = false;
    private Handler refreshHandler = new Handler();
    private Runnable refreshRunnable;
    private Handler requestHandler = new Handler();
    private RequestRunnable requestRunnable;
    private boolean doRequest = false;
    private PlacementRequestListener currentListener;
    private PlacementRequestParams currentParams;
    private PlacementRequestParams[] availableParams = new PlacementRequestParams[]{};

    private String id;
    private AdyoZoneViewListener listener = new AdyoZoneViewListener() {
        @Override
        public boolean shouldRecordImpression(Placement placement) {
            return true;
        }

        @Override
        public boolean shouldRotate(Placement placement) {
            return true;
        }

        @Override
        public boolean shouldDisplay(Placement placement) {
            return true;
        }
    };



    public AdyoZoneView(Context context) {
        super(context);
        this.context = context;
        id = UUID.randomUUID().toString();
        init();
    }

    public AdyoZoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        id = UUID.randomUUID().toString();
        init();
    }

    public AdyoZoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        id = UUID.randomUUID().toString();
        init();
    }

    private void init() {
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences("ADYO", Context.MODE_PRIVATE);
            if (sharedpreferences.getString("user_id", "").equals(""))
                new GetGAIDTask(context).execute();
        } catch (Exception ignored) {
        }
    }


    private class GetGAIDTask extends AsyncTask<String, Integer, String> {

        private Context context;

        public GetGAIDTask(Context context) {

            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            AdvertisingIdClient.Info adInfo;
            adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
                if (adInfo.isLimitAdTrackingEnabled()) // check if user has opted out of tracking
                    return "";
                return adInfo.getId();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {

            SharedPreferences sharedpreferences = context.getSharedPreferences("ADYO", Context.MODE_PRIVATE);

            sharedpreferences.edit().putString("user_id", s).apply();
        }
    }


    /**
     * This will make a request to fetch a placement for a random set of parameters and if successful
     * load it into the AdyoZoneView
     *
     * @param params the params used to make the request
     */
    public void requestRandomPlacement(Activity activity, final PlacementRequestParams[] params, @Nullable final PlacementRequestListener placementRequestListener) {

        availableParams = params;

        if (params.length > 0) {
            int index = new Random().nextInt(params.length);

            requestPlacement(activity, params[index], placementRequestListener);
        }


    }

    /**
     * This will make a request to fetch a placement for a random set of parameters and if successful
     * load it into the AdyoZoneView
     *
     * @param params the params used to make the request
     */
    public void requestRandomPlacement(Activity activity, final PlacementRequestParams[] params) {

        requestRandomPlacement(activity, params, null);
    }


    /**
     * This will make a request to fetch a placement and if successful
     * load it into the AdyoZoneView
     *
     * @param params the params used to make the request
     */
    public void requestPlacement(Activity activity, final PlacementRequestParams params) {


        availableParams = new PlacementRequestParams[]{};

        requestPlacement(activity, params, null);
    }


    /**
     * This will make a request to fetch a placement and if successful
     * load it into the AdyoZoneView
     *
     * @param params                   the params used to make the request
     * @param placementRequestListener the listener called when a placement has been requested
     */
    public void requestPlacement(final Activity activity, final PlacementRequestParams params, @Nullable final PlacementRequestListener placementRequestListener) {

        currentParams = params;

        if (currentParams.getCreativeType() == null || currentParams.getCreativeType().length == 0)
            currentParams.setCreativeType(new String[]{"image", "rich-media", "tag"});

        //If the view has not het layed itself out and the params does not
        //specify a width/height, we need to place the call on hold until the layout is done.
        if ((width == 0 && params.getWidth() == null) || (height == 0 && params.getHeight() == null)) {
            doRequest = true;

            requestRunnable = new RequestRunnable(context, params, placementRequestListener) {
                @Override
                public void run() {
                    requestPlacement((Activity) context, this.params, this.listener);
                }
            };


        } else {

            doRequest = false;
            // Intercept params
            // The width and height of the view will be taken into account if no other values are specified
            if (params.getWidth() == null) {
                params.setWidth(width);
            }

            if (params.getHeight() == null) {
                params.setHeight(height);
            }

            currentListener = new PlacementRequestListener() {
                @Override
                public void onRequestComplete(boolean isFound, Placement placement) {

                    if (placementRequestListener != null) {
                        placementRequestListener.onRequestComplete(isFound, placement);
                    }

                    if (isFound) {
                        currentPlacement = placement;
                        //Check if the ad should be loaded into the webview
                        if(listener.shouldDisplay(currentPlacement)) {
                            loadPlacement();
                        }
                        else
                        {
                            //Call afterCreativeLoaded to handle impression and refreshing
                            afterCreativeLoaded();

                        }
                    }
                }

                @Override
                public void onRequestError(String error) {

                    if (placementRequestListener != null) {
                        placementRequestListener.onRequestError(error);
                    }
                }
            };

            Adyo.requestPlacement(activity, currentParams, currentListener);
        }


    }


    private void loadPlacement() {
        WebView webView;

        try {
            webView = new WebView(context);
        }
        catch (Exception e)
        {
            webView = new LollipopFixedWebView(context);
        }
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        removeAllViews();
        addView(webView);

        webView.setBackgroundColor(Color.TRANSPARENT);

        if (currentPlacement.getCreativeType() == Placement.CREATIVE_TYPE_IMAGE) {

            //The AdyoZone becomes a ImageView to handle Images

            String url = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<meta charset=\"UTF-8\">" +
                    "<meta name=\"viewport\" content=\"initial-scale=" + 1.0 + "\"/>" +
                    "<style type=\"text/css\">" +
                    "html{margin:0;padding:0;}" +
                    "body {" +
                    "margin: 0;" +
                    "padding: 0;" +
                    "font-size: 90%;" +
                    "line-height: 1.6;" +
                    "-webkit-touch-callout: none;" +
                    "-webkit-user-select: none;" +
                    "}" +
                    "img {" +
                    "position: absolute;" +
                    "top: 0;" +
                    "bottom: 0;" +
                    "left: 0;" +
                    "right: 0;" +
                    "margin: auto;" +
                    "max-width: 100%;" +
                    "max-height: 100%;" +
                    "}" +
                    "</style>" +
                    "</head>" +
                    "<body id=\"page\">" +
                    "<img src='" + currentPlacement.getCreativeUrl() + "'/>" +
                    "</body></html>";



            webView.loadData(url, "text/html; charset=UTF-8", null);

        } else if (currentPlacement.getCreativeType() == Placement.CREATIVE_TYPE_RICH_MEDIA) {


            String url = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<meta name=\"viewport\" content=\"initial-scale=" + 1.0 + "\"/>" +
                    "<meta charset=\"UTF-8\">" +
                    "<style type=\"text/css\">" +
                    "html {margin:0; padding:0; height:100%}" +
                    "body {background: none; margin: 0; padding: 0; height:100%}" +
                    "iframe {width: 100%; height: 100%; background: none; -webkit-touch-callout: none !important; -webkit-user-select: none !important; -webkit-tap-highlight-color: rgba(0,0,0,0) !important}" +
                    "</style>" +
                    "</head>" +
                    "<body id=\"page\">" +
                    "<iframe src='" + currentPlacement.getCreativeUrl() + "' frameBorder=\"0\" ></iframe>" +
                    "</body></html>";

            //The AdyoZone becomes a WebView to handle Rich Media
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setVerticalScrollBarEnabled(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            webView.loadData(url, "text/html; charset=UTF-8", null);


        } else if (currentPlacement.getCreativeType() == Placement.CREATIVE_TYPE_TAG) {

            //Get the aspect ratio of the webview
            double ratio = width/(height * 1.0);
            double calHeight = currentPlacement.getWidth() / ratio;

            String url =  "<!DOCTYPE html>" +
                    "<html class=\"main\">" +
                    "<head>" +
                    "<meta name=\"viewport\" content=\"initial-scale=" + 1.0 + "\"/>" +
                    "<meta charset=\"UTF-8\">" +
                    "<style type=\"text/css\">" +
                    "html.main {margin:0; padding:0; height:" + height +"px;}" +
                    "body { margin: 0; padding: 0; height:" + height +"px;}" +
                    ".outer-div {position: relative;" +
                    "height:" +  calHeight  + "px; width:100%;" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class=\"outer-div\">" +
                    currentPlacement.getCreativeHtml() +
                    "</div>" +
                    "</body></html>";

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setInitialScale(1);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setVerticalScrollBarEnabled(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            if (currentPlacement.getHtmlDomain() != null)
                webView.loadDataWithBaseURL(currentPlacement.getHtmlDomain(), url, "text/html", "UTF-8", null);
            else
                webView.loadData(url, "text/html; charset=UTF-8", null);
        }


        if (currentPlacement.getClickUrl() != null) {

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Adyo.recordClicks((Activity) context, currentPlacement);
                }
            });
        }

        AdyoZoneViewWebViewClient adyoWebClient = new AdyoZoneViewWebViewClient();
        webView.setWebViewClient(adyoWebClient);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
            {
                WebView newWebView = new WebView(getContext());
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }
        });



        adyoWebClient.setPlacement(currentPlacement, currentParams, currentListener);

    }

    /**
     * Clears the ad in the view
     */
    public void clearView() {
        removeAllViews();
    }


    /**
     * Clears the ad in the view
     */
    public void reset() {
        refreshHandler.removeCallbacks(refreshRunnable);
    }


    /**
     * Custom WebViewClient used for Rich Media Creative
     */
    private class AdyoZoneViewWebViewClient extends AdyoWebViewClient {


        AdyoZoneViewWebViewClient() {
            super(context);
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            Log.d("ADYO_ZONE_VIEW_" + params.getZoneId(), "Loading creative");
            view.setVisibility(GONE);

            super.onPageStarted(view, url, favicon);

        }


        @Override
        public void onPageFinished(WebView view, String url) {

            CookieSyncManager.getInstance().sync();

            view.setVisibility(VISIBLE);

            afterCreativeLoaded();

        }


    }

    /**
     * Common function between creative types to handle when the creative is loaded
     */
    private void afterCreativeLoaded() {


        //Log third party impression

        if(listener.shouldRecordImpression(currentPlacement))
            Adyo.recordImpression(context, currentPlacement, null);


        //If we have gotten a placement back from the call and it has a refresh_after property greater than 0
        // we will do the call again in x seconds
        if(listener.shouldRotate(currentPlacement))
            refreshPlacement();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Save the width and height to be used as parameters for the getPlacement request with non
        //are specified

        final WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display d = w.getDefaultDisplay();
        final DisplayMetrics m = new DisplayMetrics();
        d.getMetrics(m);

        int density = (m.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

        width = convertPixelsToDp(MeasureSpec.getSize(widthMeasureSpec), context) * density;
        height = convertPixelsToDp(MeasureSpec.getSize(heightMeasureSpec), context) * density;

//        width = MeasureSpec.getSize(widthMeasureSpec)  ;
//        height = MeasureSpec.getSize(heightMeasureSpec);

        if (doRequest) {
            doRequest = false;
            requestHandler.post(requestRunnable);
        }

    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(int px, Context context){

        final WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display d = w.getDefaultDisplay();
        final DisplayMetrics m = new DisplayMetrics();
        d.getMetrics(m);

        return px / (m.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (currentPlacement != null && currentPlacement.getClickUrl() != null) {
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public void setPausePlacement(boolean isPaused) {

        if (!isPaused && isPaused != this.isPaused)
            if(listener.shouldRotate(currentPlacement))
                refreshPlacement();

        this.isPaused = isPaused;

        Log.d("ADYO", "Adyo Zone View " + id + " is now paused: " + isPaused);

    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        setPausePlacement(!hasWindowFocus);
        super.onWindowFocusChanged(hasWindowFocus);
    }


    @Override
    protected void onAttachedToWindow() {
        setPausePlacement(false);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        setPausePlacement(true);
        super.onDetachedFromWindow();
    }


    public void refreshPlacement() {

        if (currentPlacement != null) {

            if (currentPlacement.getRefreshAfter() > 0) {
                refresh();

                Log.d("ADYO", "Placement " + id + " will be refreshed in " + currentPlacement.getRefreshAfter() + "seconds");
            }
        }

    }


    private void refresh() {
        class RefreshRunnable implements Runnable {

            private Context context;
            private PlacementRequestParams currentParams;
            private PlacementRequestListener currentListener;


            private RefreshRunnable(Context context, PlacementRequestParams currentParams, PlacementRequestListener currentListener) {
                this.context = context;
                this.currentParams = currentParams;
                this.currentListener = currentListener;
            }

            public void run() {
                if (!isPaused) {

                    Log.d("ADYO", "Adyo Zone View " + id + " is not paused. Requesting placement");

                    Adyo.requestPlacement(this.context, this.currentParams, this.currentListener);

                } else {
                    Log.d("ADYO", "Adyo Zone View " + id + " is paused");
                }
            }
        }

        refreshHandler.removeCallbacks(refreshRunnable);


        //If we have an array of available params we pick a random one out of the list
        if (availableParams.length > 0) {
            int index = new Random().nextInt(availableParams.length);
            currentParams = availableParams[index];
        }

        //Else just use the currentParams we have
        refreshRunnable = new RefreshRunnable(context, currentParams, currentListener);

        refreshHandler.postDelayed(refreshRunnable,
                currentPlacement.getRefreshAfter() * 1000);
    }


    private int getScale(int webViewWidth) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        Double val = new Double(webViewWidth) / new Double(width);
        val = val * 100d;
        return val.intValue();
    }


    public void setAdyoZoneViewListener(AdyoZoneViewListener listener) {
        this.listener = listener;
    }

    public interface AdyoZoneViewListener {

        boolean shouldRecordImpression(Placement placement);
        boolean shouldRotate(Placement placement);
        boolean shouldDisplay(Placement placement);

    }
}
