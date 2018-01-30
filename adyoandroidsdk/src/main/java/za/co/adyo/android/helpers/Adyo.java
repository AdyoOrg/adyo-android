package za.co.adyo.android.helpers;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

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

    private static Handler handler;

    /**
     * @param params   parameters that will go into the request body
     * @param listener the listener will be called when request is completed or fails
     */
    public static void requestPlacement(final Context context, final PlacementRequestParams params, final PlacementRequestListener listener) {

        if(handler == null)
            handler = new Handler();
        else
            handler.removeCallbacksAndMessages(null);

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

            if(placement.getAppTarget() == Placement.APP_TARGET_POPUP || placement.getAppTarget() == Placement.APP_TARGET_INSIDE) {

               Adyo.showAlertDialog(activity.getFragmentManager(), placement.getClickUrl());

            }
            else
            {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(placement.getClickUrl()));
                activity.startActivity(i);
            }
            Log.d("ADYO", "Placement click recorded");
        }

    }


    /**
     * @param context
     * @param placement placement to refresh
     * @param params placement request parameters
     * @param listener placement request listener
     */
    public static void refreshPlacement(final Context context, final Placement placement,
                                        PlacementRequestParams params, PlacementRequestListener listener) {

        if(placement.getRefreshAfter() > 0) {

            handler.postDelayed(
                    new RefreshRunnable(context, params, listener),
                    placement.getRefreshAfter() * 1000);

            Log.d("ADYO", "Placement will be refreshed in " + placement.getRefreshAfter() + "seconds");
        }

    }

    public static void showAlertDialog(FragmentManager fm, String url, @StyleRes int theme) {

        AdyoPopupView alertDialog = AdyoPopupView.newInstance(url, theme);
        alertDialog.show(fm, "fragment_alert");
    }

    public static void showAlertDialog(FragmentManager fm, String url) {
        AdyoPopupView alertDialog = AdyoPopupView.newInstance(url);
        alertDialog.show(fm, "fragment_alert");
    }



}
