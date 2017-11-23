package za.co.adyo.android.requests;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 * RecordThirdPartyImpressionRequest
 * Get method call to the placement's third party impression url
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/13/17
 */

public class RecordThirdPartyImpressionRequest extends AdyoRestRequest {

    private String impressionUrl;

    public RecordThirdPartyImpressionRequest(String impressionUrl) {
        this.impressionUrl = impressionUrl;

        Log.d("ADYO_REQUEST", "Record third party impression request started");
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
        map.put("content-type","application/json");
        return map;
    }

    @Override
    protected byte[] getBody(Context context) {

        return null;
    }

    @Override
    protected void onComplete(Context context, InputStream response) throws IOException {

        Log.d("ADYO_REQUEST", "Record third party impression request completed");

    }

    @Override
    protected void onError(Context context) {

        Log.d("ADYO_REQUEST", "Record third party impression request failed");

    }

    @Override
    protected String getMethod(Context context) {
        return METHOD_GET;
    }

}
