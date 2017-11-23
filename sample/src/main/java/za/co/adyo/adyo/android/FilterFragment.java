package za.co.adyo.adyo.android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

import za.co.adyo.android.requests.PlacementRequestParams;

/**
 * FilterFragment
 * Allowing users to play around with placement call settings
 *
 * @author UnitX, marilie
 * @version 1.0, 11/21/17
 */

public class FilterFragment extends BottomSheetDialogFragment {

    private final static long NETWORK_ID = 13;
    private final static long ZONE_ID_1 = 4;
    private final static long ZONE_ID_2 = 5;

    private OnFilterListener listener;
    private EditText networkIdEdit;
    private EditText zoneIdEdit;
    private EditText userIdEdit;
    private EditText keywordsEdit;
    private EditText widthEdit;
    private EditText heightEdit;
    private Button filterButton;
    private Button resetButton;

    public static FilterFragment newInstance() {

        return new FilterFragment();
    }


    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };



    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.fragment_filter, null);
        dialog.setContentView(contentView);

        networkIdEdit = contentView.findViewById(R.id.network_id_edit);
        zoneIdEdit = contentView.findViewById(R.id.zone_id_edit);
        userIdEdit = contentView.findViewById(R.id.user_id_edit);
        keywordsEdit = contentView.findViewById(R.id.keyword_edit);
        widthEdit = contentView.findViewById(R.id.width_edit);
        heightEdit = contentView.findViewById(R.id.height_edit);
        filterButton = contentView.findViewById(R.id.filter_button);
        resetButton = contentView.findViewById(R.id.reset_button);

        resetFilters();

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String [] keywords = new String[0];
                if(!keywordsEdit.getText().toString().equals(""))
                {
                    List<String> keywordsList = Arrays.asList(keywordsEdit.getText().toString().split("\\s*,\\s*"));
                    keywords = keywordsList.toArray(new String[keywordsList.size()]);
                }

                PlacementRequestParams params = new PlacementRequestParams(getActivity(),
                        Long.valueOf(networkIdEdit.getText().toString()),
                        Long.valueOf(zoneIdEdit.getText().toString()),
                        userIdEdit.getText().toString(),
                        keywords,
                        Integer.valueOf(widthEdit.getText().toString()),
                        Math.min(Integer.parseInt(heightEdit.getText().toString()), 400)
                );

                listener.onFilter(params);
            }
        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilters();
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (MainActivity)activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (MainActivity)context;
    }

    public interface OnFilterListener {

        void onFilter(PlacementRequestParams params);
    }


    private void resetFilters()
    {
        int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        int heightPixels = 400;


        networkIdEdit.setText(String.valueOf(NETWORK_ID));
        zoneIdEdit.setText(String.valueOf(ZONE_ID_1));
        userIdEdit.setText("YPf7G7BXtFCdEn");
        keywordsEdit.setText("");
        widthEdit.setText(String.valueOf(widthPixels));
        heightEdit.setText(String.valueOf(heightPixels));
    }


}
