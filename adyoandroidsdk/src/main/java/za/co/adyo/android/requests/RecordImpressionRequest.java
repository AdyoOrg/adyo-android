package za.co.adyo.android.requests;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import za.co.adyo.android.BuildConfig;
import za.co.adyo.android.R;

/**
 * RecordImpressionRequest
 * Get method call to the placement's impression url
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/13/17
 */

public class RecordImpressionRequest extends AdyoRestRequest {

    private String impressionUrl;

    public RecordImpressionRequest(String impressionUrl) {
        this.impressionUrl = impressionUrl;

        Log.d("ADYO_REQUEST", "Record impression request started");
    }

    @Override
    protected MediaType getMediaType(Context context) {
        return null;
    }

    @Override
    protected String getURL(Context context) {
        return impressionUrl;
    }

    @Override
    protected Map<String, String> getHeaders(Context context) {

        Map<String, String> map = new HashMap<>();

        try {
            String userAgent = new WebView(context).getSettings().getUserAgentString();
            map.put("User-Agent", userAgent);
        } catch (Exception ignore) {

        }

        map.put("content-type", "application/json");
        map.put("X-Adyo-Platform", context.getResources().getBoolean(R.bool.isTablet) ? "Tablet" : "Phone");
        map.put("X-Adyo-Model", getDeviceName());
        map.put("X-Adyo-OS-Version", String.valueOf(Build.VERSION.RELEASE));
        map.put("X-Adyo-OS", "Android");
        map.put("X-Adyo-SDK-Version", BuildConfig.VERSION_NAME);

        return map;
    }

    @Override
    protected byte[] getBody(Context context) {

        return null;
    }

    @Override
    protected void onComplete(Context context, InputStream response) throws IOException {

        Log.d("ADYO_REQUEST", "Record impression request completed");
    }

    @Override
    protected void onError(Context context) {

        Log.d("ADYO_REQUEST", "Record impression request failed");
    }

    @Override
    protected String getMethod(Context context) {
        return METHOD_GET;
    }


    /**
     * Returns the consumer friendly device name
     */
    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

}
