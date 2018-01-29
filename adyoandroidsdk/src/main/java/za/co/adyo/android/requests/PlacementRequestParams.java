package za.co.adyo.android.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.UUID;

/**
 * PlacementRequestParams
 * Models a placement with all of its properties
 *
 * @author Marilie, UnitX
 * @version 1.0, 11/13/17
 */

public class PlacementRequestParams implements Parcelable {

    private long networkId;
    private long zoneId;
    private String userId;
    private String[] keywords;
    @Nullable
    private Integer width;
    @Nullable
    private Integer height;
    @Nullable
    private JSONObject customKeywords;

    public PlacementRequestParams(Context context, long networkId, long zoneId, @Nullable String userId, @Nullable String[] keywords, @Nullable Integer width, @Nullable Integer height, @Nullable JSONObject customKeywords) {
        this.networkId = networkId;
        this.zoneId = zoneId;
        this.customKeywords = customKeywords;

        // If a userId is not provide, a random id will be generated and saved in SharedPreferences for later use.
        // If a userId is provided, it is saved in SharedPreferences for later when the userId is again not provided.
        SharedPreferences sharedpreferences = context.getSharedPreferences("ADYO", Context.MODE_PRIVATE);
        if (userId == null) {

            String tempUserId = UUID.randomUUID().toString();
            this.userId = sharedpreferences.getString("user_id", tempUserId);
            sharedpreferences.edit().putString("user_id", tempUserId).apply();
        } else {

            this.userId = userId;
            sharedpreferences.edit().putString("user_id", userId).apply();
        }

        if (keywords == null)
            this.keywords = new String[0];
        else
            this.keywords = keywords;


        this.width = width;
        this.height = height;
    }

    public PlacementRequestParams(Context context, long networkId, long zoneId, @Nullable String userId) {
        this(context, networkId, zoneId, userId, null, null, null, null);
    }

    protected PlacementRequestParams(Parcel in) {
        networkId = in.readLong();
        zoneId = in.readLong();
        userId = in.readString();
        keywords = in.createStringArray();
        if (in.readByte() == 0) {
            width = null;
        } else {
            width = in.readInt();
        }
        if (in.readByte() == 0) {
            height = null;
        } else {
            height = in.readInt();
        }
    }

    public static final Creator<PlacementRequestParams> CREATOR = new Creator<PlacementRequestParams>() {
        @Override
        public PlacementRequestParams createFromParcel(Parcel in) {
            return new PlacementRequestParams(in);
        }

        @Override
        public PlacementRequestParams[] newArray(int size) {
            return new PlacementRequestParams[size];
        }
    };

    /**
     * @return network id corresponding to the placement
     */
    public long getNetworkId() {
        return networkId;
    }

    /**
     * @param networkId network id corresponding to the placement
     */
    public void setNetworkId(long networkId) {
        this.networkId = networkId;
    }

    /**
     * @return zone id corresponding to the placement
     */
    public long getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId zone id corresponding to the placement
     */
    public void setZoneId(long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return current user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId current user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return a list of keywords associated with the placement
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * @param keywords a list of keywords associated with the placement
     */
    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    /**
     * @return width of the placement needed
     */
    @Nullable
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width width of the placement needed
     */
    public void setWidth(@Nullable Integer width) {
        this.width = width;
    }

    /**
     * @return height of the placement needed
     */
    @Nullable
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height height of the placement needed
     */
    public void setHeight(@Nullable Integer height) {
        this.height = height;
    }


    /**
     * @return JSON object of custom keywords
     */
    @Nullable
    public JSONObject getCustomKeywords() {
        return customKeywords;
    }

    /**
     * @param customKeywords JSON object of custom keywords
     */
    public void setCustomKeywords(@Nullable JSONObject customKeywords) {
        this.customKeywords = customKeywords;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(networkId);
        parcel.writeLong(zoneId);
        parcel.writeString(userId);
        parcel.writeStringArray(keywords);
        if (width == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(width);
        }
        if (height == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(height);
        }
    }
}
