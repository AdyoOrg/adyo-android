package za.co.adyo.adyo.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import za.co.adyo.android.requests.PlacementRequestParams;
import za.co.adyo.android.views.AdyoZoneView;

public class AdyoZoneViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adyo_zone_view);

        AdyoZoneView zone1 = (AdyoZoneView) findViewById(R.id.adyo_zone_1);
        AdyoZoneView zone2 = (AdyoZoneView) findViewById(R.id.adyo_zone_2);


        PlacementRequestParams params1 = new PlacementRequestParams(
                AdyoZoneViewActivity.this,
                getResources().getInteger(R.integer.adyo_network_id),
                getResources().getInteger(R.integer.adyo_zone_id_1),
                null
        );
        PlacementRequestParams params2 = new PlacementRequestParams(
                AdyoZoneViewActivity.this,
                getResources().getInteger(R.integer.adyo_network_id),
                getResources().getInteger(R.integer.adyo_zone_id_2),
                null
        );

        zone1.requestPlacement(params1);
        zone2.requestPlacement(params2);

    }
}
