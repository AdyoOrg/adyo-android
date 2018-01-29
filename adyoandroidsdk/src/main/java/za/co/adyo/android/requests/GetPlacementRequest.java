package za.co.adyo.android.requests;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import za.co.adyo.android.R;

/**
 * GetPlacementRequest
 * Post call to get an Adyo Placement
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/13/17
 */

public class GetPlacementRequest extends AdyoRestRequest {

    private PlacementRequestParams params;


    public GetPlacementRequest(PlacementRequestParams params) {
        this.params = params;
        Log.d("ADYO_REQUEST", "Get placement request started");
    }

    @Override
    protected MediaType getMediaType(Context context) {
        return null;
    }

    @Override
    protected String getURL(Context context) {

        return context.getString(R.string.adyo_host) + "/serve";
    }

    @Override
    protected Map<String, String> getHeaders(Context context) {

        Map<String, String> map = new HashMap<>();
        map.put("content-type","application/json");
        return map;
    }

    @Override
    protected byte[] getBody(Context context) {

        byte[] array = new byte[0];

        JSONObject body = new JSONObject();
        try {
            body.put("network_id", params.getNetworkId());
            body.put("zone_id", params.getZoneId());
            body.put("user_id", params.getUserId());
            List<String> stringList = new ArrayList<>(Arrays.asList(params.getKeywords()));
            body.put("keywords", new JSONArray(stringList));
            if(params.getWidth() != null)
                body.put("width", params.getWidth());
            if(params.getHeight() != null)
                body.put("height", params.getHeight());

            if(params.getCustomKeywords() != null)
                body.put("custom", params.getCustomKeywords());

            array =  body.toString().getBytes("UTF-8");
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();

        }

        return  array;

    }

    @Override
    protected void onComplete(Context context, InputStream response) throws IOException {
        Log.d("ADYO_REQUEST", "Get placement request completed");
    }

    @Override
    protected void onError(Context context) {
        Log.d("ADYO_REQUEST", "Get placement request failed");
    }

    @Override
    protected String getMethod(Context context) {
        return METHOD_POST;
    }

}
