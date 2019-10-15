package za.co.adyo.android.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import za.co.adyo.android.AdyoZoneActivity;
import za.co.adyo.android.R;
import za.co.adyo.android.listeners.ImpressionRequestListener;
import za.co.adyo.android.listeners.PlacementRequestListener;
import za.co.adyo.android.models.Placement;
import za.co.adyo.android.requests.AdyoRequestCallback;
import za.co.adyo.android.requests.AdyoRequestResponse;
import za.co.adyo.android.requests.GetPlacementRequest;
import za.co.adyo.android.requests.PlacementRequestParams;
import za.co.adyo.android.requests.RecordImpressionRequest;
import za.co.adyo.android.requests.RecordThirdPartyImpressionRequest;
import za.co.adyo.android.views.AdyoPopupView;

/**
 * Adyo
 * Helper class to make all Adyo calls
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/13/17
 */

public class Adyo {

    /**
     * @param params   parameters that will go into the request body
     * @param listener the listener will be called when request is completed or fails
     */
    public static void requestPlacement(final Context context, final PlacementRequestParams params, final PlacementRequestListener listener) {



        new GetPlacementRequest(params).execute(context, GetPlacementRequest.class.getName(), new AdyoRequestCallback() {
            @Override
            public void onComplete(AdyoRequestResponse adyoRequestResponse) {
                Log.d("ADYO", "Placement fetched successfully");

                listener.onRequestComplete(adyoRequestResponse.getPlacement() != null, adyoRequestResponse.getPlacement());

            }

            @Override
            public void onFailed(AdyoRequestResponse adyoRequestResponse) {
                Log.d("ADYO", "Failed to fetch placement");
                listener.onRequestError(adyoRequestResponse.getRawResponse());
            }
        });

    }


    /**
     * @param context
     * @param placement the placement to record impressions on
     * @param listener ImpressionRequestListener callback
     */
    public static void recordImpression(Context context, final Placement placement, @Nullable final ImpressionRequestListener listener) {


        final AdyoRequestCallback callback = new AdyoRequestCallback() {

            boolean requestBoth = placement.getThirdPartyImpressionUrl() != null;
            AdyoRequestResponse impressionReqResponse = null;
            AdyoRequestResponse thirdPartyImpressionReqResponse = null;

            String impressionReqError = null;
            String thirdPartyImpressionReqError = null;

            @Override
            public void onComplete(AdyoRequestResponse adyoRequestResponse) {

                if (listener != null) {
                    // If the RecordThirdPartyImpressionRequest has also been made
                    if (requestBoth) {

                        if (adyoRequestResponse.getType().equals(RecordImpressionRequest.class.getName())) {
                            // Save the response object
                            impressionReqResponse = adyoRequestResponse;

                            // Check the Http status code to know if the request wast successful
                            if (!(adyoRequestResponse.getCode() >= 200 && adyoRequestResponse.getCode() < 400)) {

                                // Record any errors
                                impressionReqError = adyoRequestResponse.getRawResponse();
                                Log.d("ADYO", "Failed to record impression on placement");
                            } else {
                                Log.d("ADYO", "Impression recorded on placement");
                            }

                        } else {
                            // Save the response object
                            thirdPartyImpressionReqResponse = adyoRequestResponse;

                            // Check the Http status code to know if the request wast successful
                            if (!(adyoRequestResponse.getCode() >= 200 && adyoRequestResponse.getCode() < 400)) {

                                // Record any errors
                                thirdPartyImpressionReqError = adyoRequestResponse.getRawResponse();
                                Log.d("ADYO", "Failed to record third party impression on placement");
                            } else {
                                Log.d("ADYO", "Third party impression recorded on placement");
                            }
                        }

                        //If both calls have returned we can call the callback
                        if (impressionReqResponse != null && thirdPartyImpressionReqResponse != null) {

                            //If either call responded with an error, we send back an error
                            if (impressionReqError != null || thirdPartyImpressionReqError != null) {
                                listener.onRequestError(impressionReqError, thirdPartyImpressionReqError);
                            } else {
                                listener.onRequestComplete(impressionReqResponse.getCode(), thirdPartyImpressionReqResponse.getCode());
                            }


                        }
                    } else {
                        listener.onRequestComplete(adyoRequestResponse.getCode(), null);
                    }
                }


            }

            @Override
            public void onFailed(AdyoRequestResponse adyoRequestResponse) {

                if (listener != null) {
                    if (requestBoth) {

                        if (adyoRequestResponse.getType().equals(RecordImpressionRequest.class.getName())) {
                            impressionReqResponse = adyoRequestResponse;
                            impressionReqError = adyoRequestResponse.getRawResponse();

                            Log.d("ADYO", "Failed to record impression on placement");
                        } else {
                            thirdPartyImpressionReqResponse = adyoRequestResponse;
                            thirdPartyImpressionReqError = adyoRequestResponse.getRawResponse();

                            Log.d("ADYO", "Failed to record third party impression on placement");

                        }


                        if (impressionReqResponse != null && thirdPartyImpressionReqResponse != null) {

                            listener.onRequestError(impressionReqError, thirdPartyImpressionReqError);
                        }
                    } else {
                        listener.onRequestError(impressionReqError, null);
                    }
                }


            }
        };

        new RecordImpressionRequest(placement.getImpressionUrl()).execute(context, RecordImpressionRequest.class.getName(), callback);

        if (placement.getThirdPartyImpressionUrl() != null) {

            new RecordThirdPartyImpressionRequest(placement.getThirdPartyImpressionUrl()).execute(context, RecordThirdPartyImpressionRequest.class.getName(), callback);
        }

    }


    /**
     * @param activity
     * @param placement the placement that the click must be recorded on
     */
    public static void recordClicks(final Activity activity, final Placement placement) {

        if (placement.getClickUrl() != null) {

            if( placement.getAppTarget() == Placement.APP_TARGET_INSIDE) {

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(activity, Uri.parse(placement.getClickUrl()));


            }
            else if(placement.getAppTarget() == Placement.APP_TARGET_POPUP) {

               Adyo.showAlertDialog(activity.getFragmentManager(), placement.getClickUrl(), activity);

            }
            else
            {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(placement.getClickUrl()));
                    activity.startActivity(i);
                }
                catch (Exception e)
                {
                    Toast.makeText(activity, "No app found to handle this intent. Please download a browser app.", Toast.LENGTH_LONG).show();
                }

            }
            Log.d("ADYO", "Placement click recorded");
        }

    }


    public static void showAlertDialog(FragmentManager fm, String url, final Context context) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        WebView wv = new WebView(context);
        wv.loadUrl(url);

        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton(" ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                // if you do the following it will be left aligned, doesn't look
                // correct
                // button.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play,
                // 0, 0, 0);

                Drawable drawable = context.getResources().getDrawable(
                        R.drawable.adyo_ic_close);


                drawable.setColorFilter(button.getTextColors().getDefaultColor(), PorterDuff.Mode.SRC_ATOP);


                // set the bounds to place the drawable a bit right
                drawable.setBounds((int) (drawable.getIntrinsicWidth() * 0.5),
                        0, (int) (drawable.getIntrinsicWidth() * 1.5),
                        drawable.getIntrinsicHeight());
                button.setCompoundDrawables(null, null, drawable, null);

            }
        });


        alertDialog.show();


    }


}
