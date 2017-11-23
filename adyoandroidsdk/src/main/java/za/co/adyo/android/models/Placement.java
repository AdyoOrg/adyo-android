package za.co.adyo.android.models;

import android.support.annotation.Nullable;

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

    private String creativeUrl;
    private int creativeType;
    private String impressionUrl;
    @Nullable
    private String clickUrl;
    @Nullable
    private String thirdPartyImpressionUrl;
    private int refreshAfter;

    public Placement(String creativeUrl, String creativeType, String impressionUrl,
                     @Nullable String clickUrl, @Nullable String thirdPartyImpressionUrl, int refreshAfter) {

        this.creativeUrl = creativeUrl;

        switch (creativeType) {
            case "image": {
                this.creativeType = CREATIVE_TYPE_IMAGE;
                break;
            }
            case "rich-media": {
                this.creativeType = CREATIVE_TYPE_RICH_MEDIA;
                break;
            }
        }

        this.impressionUrl = impressionUrl;
        this.clickUrl = clickUrl;
        this.thirdPartyImpressionUrl = thirdPartyImpressionUrl;
        this.refreshAfter = refreshAfter;
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
}
