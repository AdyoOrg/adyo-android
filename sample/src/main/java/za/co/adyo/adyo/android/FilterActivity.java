package za.co.adyo.adyo.android;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import za.co.adyo.android.listeners.PlacementRequestListener;
import za.co.adyo.android.models.Placement;
import za.co.adyo.android.requests.PlacementRequestParams;
import za.co.adyo.android.views.AdyoZoneView;

public class FilterActivity extends AppCompatActivity implements FilterFragment.OnFilterListener {

    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private AdyoZoneView adyoZoneView;

    private PlacementRequestParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

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


        //Create a new parameter holder
        PlacementRequestParams params = new PlacementRequestParams(
                FilterActivity.this,                                      // Context
                getResources().getInteger(R.integer.adyo_network_id),   // Network Id
                getResources().getInteger(R.integer.adyo_zone_id_2),    // Zone Id
                null,                                                   // User Id (Can be Null)
                new String[0],                                          // List of Keywords
                null,                                                   // Width
                null);                                                  // Height

        adyoZoneView.requestPlacement(params, new PlacementRequestListener() {
            @Override
            public void onRequestComplete(boolean isFound, Placement placement) {
                if(!isFound)
                {
                    new AlertDialog.Builder(FilterActivity.this)
                            .setTitle("No Placement Found")
                            .setMessage("Please adjust the filters and try again")
                            .show();
                }
            }

            @Override
            public void onRequestError(String error) {

            }
        });

    }

    @Override
    public void onFilter(PlacementRequestParams params) {

        this.params = params;

        bottomSheetDialogFragment.dismiss();

        adyoZoneView.getLayoutParams().width = params.getWidth();
        adyoZoneView.getLayoutParams().height = params.getHeight();
        adyoZoneView.requestLayout();

        params.setWidth(null);
        params.setHeight(null);

        adyoZoneView.requestPlacement(params, new PlacementRequestListener() {
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


            }

            @Override
            public void onRequestError(String error) {

            }
        });

    }
}
