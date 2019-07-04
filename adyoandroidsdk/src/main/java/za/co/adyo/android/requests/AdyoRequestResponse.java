package za.co.adyo.android.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import za.co.adyo.android.models.Placement;

/**
 * AdyoRequestResponse
 * A response holder object for all adyo calls
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/13/17
 */

public class AdyoRequestResponse {

    private Integer code;
    private List<Placement> placementList;
    private String rawResponse;
    private String type;

    public AdyoRequestResponse(Integer code, String inputStream, String type) {
        if (inputStream != null) {
            createPlacementList(inputStream);
        }
        this.code = code;
        this.rawResponse = inputStream;
        this.type = type;
    }

    /**
     * @param inputStream expected json string from request to be converted to placements
     */
    private void createPlacementList(String inputStream) {
        placementList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(inputStream);

            Placement placement = new Placement(
                    jsonObject.has("creative_url") && !jsonObject.isNull("creative_url") ? jsonObject.getString("creative_url") : null,
                    jsonObject.has("creative_html") && !jsonObject.isNull("creative_html") ? jsonObject.getString("creative_html") : null,
                    jsonObject.getString("creative_type"),
                    jsonObject.has("tag_domain") && !jsonObject.isNull("tag_domain") ? jsonObject.getString("tag_domain") : null,
                    jsonObject.getString("impression_url"),
                    jsonObject.has("click_url") && !jsonObject.isNull("click_url") ? jsonObject.getString("click_url") : null,
                    jsonObject.has("third_party_impression_url") && !jsonObject.isNull("third_party_impression_url") ? jsonObject.getString("third_party_impression_url") : null,
                    jsonObject.has("refresh_after") ? jsonObject.getInt("refresh_after") : 0,
                    jsonObject.has("app_target") ? jsonObject.getString("app_target") : "default",
                    jsonObject.has("metadata") && !jsonObject.isNull("metadata") ? jsonObject.getJSONObject("metadata") : null,
                    jsonObject.has("width") ? jsonObject.getInt("width") : 0,
                    jsonObject.has("height") ? jsonObject.getInt("height") : 0
                    );

            placementList.add(placement);


        } catch (JSONException e) {

            //TODO: Check if the response is an array with multiple placements

        }
    }

    /**
     * @return HTTP status code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code HTTP status code
     */
    public void setCode(Integer code) {
        this.code = code;
    }


    /**
     * @return placement generated from request response
     */
    public Placement getPlacement() {

        return placementList.size() > 0 ? placementList.get(0) : null;
    }

    /**
     * @return raw response
     */
    public String getRawResponse() {
        return rawResponse;
    }

    /**
     * @param rawResponse raw response
     */
    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
        createPlacementList(rawResponse);
    }

    /**
     * @return the class name of the request class
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the class name of the request class
     */
    public void setType(String type) {
        this.type = type;
    }
}
