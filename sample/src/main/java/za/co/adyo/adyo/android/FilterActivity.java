package za.co.adyo.adyo.android;

import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import za.co.adyo.android.listeners.PlacementRequestListener;
import za.co.adyo.android.models.Placement;
import za.co.adyo.android.requests.PlacementRequestParams;
import za.co.adyo.android.views.AdyoZoneView;

public class FilterActivity extends AppCompatActivity implements FilterFragment.OnFilterListener {

    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private AdyoZoneView adyoZoneView;

    private PlacementRequestParams params;
    private boolean displayAd;
    private boolean recordImpression;
    private boolean rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        Button filterButton = (Button) findViewById(R.id.filter_button);


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialogFragment = FilterFragment.newInstance();
                ((FilterFragment)bottomSheetDialogFragment).setParams(params);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

            }
        });


        //*********Using the AdyoZoneView:*********

        //Fetch AdyoZoneView from xml
        adyoZoneView = (AdyoZoneView) findViewById(R.id.adyo_zone_view);


//        //Create a new parameter holder
//        PlacementRequestParams params = new PlacementRequestParams(
//                FilterActivity.this,                                      // Context
//                getResources().getInteger(R.integer.adyo_network_id),   // Network Id
//                getResources().getInteger(R.integer.adyo_zone_id_2),    // Zone Id
//                null,                                                   // User Id (Can be Null)
//                new String[0],                                          // List of Keywords
//                null,                                                   // Width
//                null,
//                null);// Height
//

        if(savedInstanceState == null) {
            filterButton.performClick();
        }
        else {
            params = savedInstanceState.getParcelable("params");
            displayAd = savedInstanceState.getBoolean("displayAd");
            recordImpression = savedInstanceState.getBoolean("recordImpression");
            rotate = savedInstanceState.getBoolean("rotate");
            onFilter(params, displayAd, recordImpression, rotate);
        }


    }

    @Override
    public void onFilter(PlacementRequestParams params, boolean displayAd, boolean recordImpression, boolean rotate) {

        this.params = params;

        if(bottomSheetDialogFragment != null)
            bottomSheetDialogFragment.dismiss();

        adyoZoneView.getLayoutParams().width = params.getWidth();
        adyoZoneView.getLayoutParams().height = params.getHeight();
        adyoZoneView.requestLayout();
        adyoZoneView.setShouldDisplay(displayAd);
        adyoZoneView.setShouldRecordImpression(recordImpression);
        adyoZoneView.setShouldRotate(rotate);

        params.setWidth(null);
        params.setHeight(null);

        adyoZoneView.clearView();
        adyoZoneView.requestPlacement(this, params, new PlacementRequestListener() {
            @Override
            public void onRequestComplete(boolean isFound, Placement placement) {
                if(!isFound)
                {
                    new AlertDialog.Builder(FilterActivity.this)
                            .setTitle("No Placement Found")
                            .setMessage("Please adjust the filters and try again")
                            .show();

                    adyoZoneView.clearView();
                }
//                else
//                {
//                    adyoZoneView.setShouldDisplay(placement.getMetadata() == null);
//                }


            }

            @Override
            public void onRequestError(String error) {

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("params", params);
        outState.putBoolean("displayAd", displayAd);
        outState.putBoolean("recordImpression", recordImpression);
        outState.putBoolean("rotate", rotate);
        super.onSaveInstanceState(outState);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
