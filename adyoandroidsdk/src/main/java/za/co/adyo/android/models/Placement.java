package za.co.adyo.android.models;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Placement
 * Models a Placement and all of its properties
 *
 * @author UnitX, marilie
 * @version 1.0, 11/13/17
 */

public class Placement {

    // @CREATIVE_TYPES
    public static final int CREATIVE_TYPE_IMAGE = 1;
    public static final int CREATIVE_TYPE_RICH_MEDIA = 2;
    public static final int CREATIVE_TYPE_TAG = 3;

    public static final int APP_TARGET_DEFAULT = 11;
    public static final int APP_TARGET_INSIDE = 12;
    public static final int APP_TARGET_POPUP = 13;

    private String creativeUrl;
    private String creativeHtml;
    private String htmlDomain;
    private int creativeType;
    private String impressionUrl;
    @Nullable
    private String clickUrl;
    @Nullable
    private String thirdPartyImpressionUrl;
    private int refreshAfter;
    private int appTarget;
    private int width;
    private int height;
    private JSONObject metadata;
    private JSONArray matchedKeywords;

    public Placement(@Nullable String creativeUrl, @Nullable String htmlUrl, String creativeType, @Nullable String htmlDomain, String impressionUrl,
                     @Nullable String clickUrl, @Nullable String thirdPartyImpressionUrl, int refreshAfter, String appTarget, JSONObject metadata, int width, int height, JSONArray matchedKeywords) {

        this.creativeUrl = creativeUrl;
        this.creativeHtml = htmlUrl;
        this.htmlDomain = htmlDomain;

        switch (creativeType) {
            case "image": {
                this.creativeType = CREATIVE_TYPE_IMAGE;
                break;
            }
            case "rich-media": {
                this.creativeType = CREATIVE_TYPE_RICH_MEDIA;
                break;
            }
            case "tag": {
                this.creativeType = CREATIVE_TYPE_TAG;
                break;
            }
        }

        this.impressionUrl = impressionUrl;
        this.clickUrl = clickUrl;
        this.thirdPartyImpressionUrl = thirdPartyImpressionUrl;
        this.refreshAfter = refreshAfter;
        this.metadata = metadata;
        this.matchedKeywords = matchedKeywords;
        this.width = width;
        this.height = height;

        // Used to determine how the click url will be displayed
        switch (appTarget) {
            case "popup":
                this.appTarget = APP_TARGET_POPUP;
                break;
            case "inside":
                this.appTarget = APP_TARGET_INSIDE;
                break;
            default:
                this.appTarget = APP_TARGET_DEFAULT;
                break;
        }

    }

    /**
     * @return creative url to be displayed in the zone view
     */
    public String getCreativeUrl() {
        return creativeUrl;
    }

    /**
     * @param creativeUrl creative url to be displayed in the zone view
     */
    public void setCreativeUrl(String creativeUrl) {
        this.creativeUrl = creativeUrl;
    }

    /**
     * @return the url that the user will be redirect to when the creative is clicked on
     */
    @Nullable
    public String getClickUrl() {
        return clickUrl;
    }

    /**
     * @param clickUrl the url that the user will be redirect to when the creative is clicked on
     */
    public void setClickUrl(@Nullable String clickUrl) {
        this.clickUrl = clickUrl;
    }

    /**
     * @return the url to log impressions of a specific creative
     */
    public String getImpressionUrl() {
        return impressionUrl;
    }

    /**
     * @param impressionUrl the url to log impression count of a specific creative
     */
    public void setImpressionUrl(String impressionUrl) {
        this.impressionUrl = impressionUrl;
    }

    /**
     * @return an additional, third party url to verify impressions on a creative
     */
    @Nullable
    public String getThirdPartyImpressionUrl() {
        return thirdPartyImpressionUrl;
    }

    /**
     * @param thirdPartyImpressionUrl an additional, third party url to verify impressions on a creative
     */
    public void setThirdPartyImpressionUrl(@Nullable String thirdPartyImpressionUrl) {
        this.thirdPartyImpressionUrl = thirdPartyImpressionUrl;
    }

    /**
     * @return the type of creative (See @CREATIVE_TYPES above)
     */
    public int getCreativeType() {
        return creativeType;
    }

    /**
     * @param creativeType the type of creative (See @CREATIVE_TYPES above)
     */
    public void setCreativeType(int creativeType) {
        this.creativeType = creativeType;
    }

    /**
     * @return time to wait before refreshing the ad in seconds
     */
    public int getRefreshAfter() {
        return refreshAfter;
    }

    /**
     * @param refreshAfter time to wait before refreshing the ad in seconds
     */
    public void setRefreshAfter(int refreshAfter) {
        this.refreshAfter = refreshAfter;
    }

    /**
     * @return  app target enum
     */
    public int getAppTarget() {
        return appTarget;
    }

    /**
     *
     * @param appTarget app target enum
     */
    public void setAppTarget(int appTarget) {
        this.appTarget = appTarget;
    }

    /**
     * @return  html url to be displayed in the zone view
     */
    public String getCreativeHtml() {
        return creativeHtml;
    }

    /**
     *
     * @param creativeHtml html url to be displayed in the zone view
     */
    public void setCreativeHtml(String creativeHtml) {
        this.creativeHtml = creativeHtml;
    }

    public String getHtmlDomain() {
        return htmlDomain;
    }

    public void setHtmlDomain(String htmlDomain) {
        this.htmlDomain = htmlDomain;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public JSONArray getMatchedKeywords() {
        return matchedKeywords;
    }

    public void setMatchedKeywords(JSONArray matchedKeywords) {
        this.matchedKeywords = matchedKeywords;
    }
}
