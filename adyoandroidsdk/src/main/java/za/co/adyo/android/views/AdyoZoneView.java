package za.co.adyo.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import za.co.adyo.android.R;
import za.co.adyo.android.helpers.Adyo;
import za.co.adyo.android.helpers.AdyoWebViewClient;
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

    private int backgroundColor = -1;
    private int width = 0;
    private int height = 0;
    private Runnable layoutRunnable = null;

    public AdyoZoneView(Context context) {
        super(context);
        init(null);
    }

    public AdyoZoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AdyoZoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    /**
     * Initialize AdyoZoneView
     *
     * @param attrs the attribute set used to get property values for the AdyoZoneView
     */
    private void init(AttributeSet attrs) {

        if(attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.AdyoZoneView, 0, 0);
            try {
                backgroundColor = ta.getResourceId(R.styleable.AdyoZoneView_android_background, android.R.color.white);
            } finally {
                ta.recycle();
            }
        }
    }

    /**
     * This will make a request to fetch a placement and if successful
     * load it into the AdyoZoneView
     * @param params the params used to make the request
     */
    public void requestPlacement(final PlacementRequestParams params)
    {
        requestPlacement(params, null);
    }


    /**
     * This will make a request to fetch a placement and if successful
     * load it into the AdyoZoneView
     * @param params the params used to make the request
     * @param placementRequestListener the listener called when a placement has been requested
     */
    public void requestPlacement(final PlacementRequestParams params, final PlacementRequestListener placementRequestListener)
    {

        //If the view has not het layed itself out and the params does not
        //specify a width/height, we need to place the call on hold until the layout is done.
        if( (width == 0 && params.getWidth() == null) || (height == 0 && params.getHeight() == null) )
        {
            layoutRunnable = new Runnable()
            {

                @Override
                public void run() {
                    requestPlacement(params, placementRequestListener);
                }
            };

        }
        else
        {

            // Intercept params
            // The width and height of the view will be taken into account if no other values are specified
            if(params.getWidth() == null)
            {
                params.setWidth(width);
            }

            if(params.getHeight() == null)
            {
                params.setHeight(height);
            }

            final PlacementRequestListener listener = new PlacementRequestListener() {
                @Override
                public void onRequestComplete(boolean isFound, Placement placement) {

                    if(placementRequestListener != null)
                    {
                        placementRequestListener.onRequestComplete(isFound, placement);
                    }

                    if(isFound)
                    {
                        loadPlacement(placement, params, this);
                    }
                }

                @Override
                public void onRequestError(String error) {

                    if(placementRequestListener != null)
                    {
                        placementRequestListener.onRequestError(error);
                    }
                }
            };

            Adyo.requestPlacement(getContext(), params, listener);
        }


    }


    /**
     * @param placement the placement to be loaded into the view
     */
    private void loadPlacement(final Placement placement, final PlacementRequestParams params, final PlacementRequestListener listener) {

        View view;
        if (placement.getCreativeType() == Placement.CREATIVE_TYPE_RICH_MEDIA) {

            //The AdyoZone becomes a WebView to handle Rich Media
            view = new WebView(getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            removeAllViews();
            addView(view);
            requestLayout();

            WebView webView = (WebView) view;
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(false);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setDomStorageEnabled(true);


            AdyoZoneViewWebViewClient adyoWebClient = new AdyoZoneViewWebViewClient();
            webView.setWebViewClient(adyoWebClient);
            adyoWebClient.setBackgroundColor(backgroundColor);
            adyoWebClient.setPlacement(placement, params, listener);

            setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            setScrollbarFadingEnabled(true);
            setVerticalScrollBarEnabled(false);
            setHorizontalScrollBarEnabled(false);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            webView.setInitialScale(1);

            webView.loadUrl(placement.getCreativeUrl());

            if(placement.getClickUrl() != null) {
                webView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            Adyo.recordClicks(getContext(), placement);
                            return true;
                        }


                        return false;
                    }
                });
            }


        } else if (placement.getCreativeType() == Placement.CREATIVE_TYPE_IMAGE) {

            //The AdyoZone becomes a ImageView to handle Images
            view = new ImageView(getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            removeAllViews();
            addView(view);
            ImageView imageView = (ImageView) view;
            requestLayout();

            Picasso.with(getContext()).load(placement.getCreativeUrl()).fit().centerInside().into(imageView, new Callback() {

                @Override
                public void onSuccess() {
                    Log.d("ADYO_ZONE_VIEW", "Loading creative finished");
                    onCreativeLoaded(placement, params, listener);
                }

                @Override
                public void onError() {
                    Log.d("ADYO_ZONE_VIEW", "Loading creative failed");
                }


            });

            //Only a creative of type IMAGE can be clicked on
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Adyo.recordClicks(getContext(), placement);
                }
            });

        }

    }

    /**
     *  Clears the ad in the view
     */
    public void clearView() {
        removeAllViews();
    }


    /**
     * Custom WebViewClient used for Rich Media Creative
     */
    private class AdyoZoneViewWebViewClient extends AdyoWebViewClient {


        private String backgroundColor;

        AdyoZoneViewWebViewClient() {
            super(getContext());
        }


        public void setBackgroundColor(int backgroundColor) {

            if(backgroundColor == -1)
                this.backgroundColor = "#FFFFFF";
            else
                this.backgroundColor = "#" + (Integer.toHexString(ContextCompat.getColor(getContext(), backgroundColor)).substring(2));

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            Log.d("ADYO_ZONE_VIEW", "Loading creative");
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onPageFinished(WebView view, String url) {


            Log.d("ADYO_ZONE_VIEW", "Loading creative finished");

            //To set the background of the WebView we need to use javascript after the page is loaded
            String command = "javascript:(function() {" +
                        "document.getElementsByTagName(\"body\")[0].style.backgroundColor = \"" + backgroundColor + "\";" +
                    "})()";
            view.loadUrl(command);

            onCreativeLoaded(placement, params, listener);

        }
    }


    /**
     * Common function between creative types to handle when the creative is loaded
     *
     * @param placement placement being added to the AdyoZoneView
     */
    private void onCreativeLoaded(Placement placement, PlacementRequestParams params, PlacementRequestListener listener) {


        //Log third party impression
        Adyo.recordImpression(getContext(), placement, null);


        //If we have gotten a placement back from the call and it has a refresh_after property greater than 0
        // we will do the call again in x seconds
        Adyo.refreshPlacement(getContext(), placement, params, listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Save the width and height to be used as parameters for the getPlacement request with non
        //are specified
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        if(layoutRunnable != null)
        {
            new Handler().post(layoutRunnable);
            layoutRunnable = null;
        }

    }


    private interface PicassoCallback extends Callback {

        void onSuccess(Placement placement);
    }


}
