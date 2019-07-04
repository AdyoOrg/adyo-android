package za.co.adyo.adyo.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
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

    private OnFilterListener listener;
    private EditText networkIdEdit;
    private EditText zoneIdEdit;
    private EditText userIdEdit;
    private EditText keywordsEdit;
    private EditText widthEdit;
    private EditText heightEdit;
    private SwitchCompat displaySwitch;
    private SwitchCompat impressionSwitch;
    private SwitchCompat rotateSwitch;

    //Custom Keywords (CKW)
    private LinearLayout CKWContainer;
    private EditText CKWKeyEdit;
    private EditText CKWValueEdit;
    private EditText currentCKWKeyEdit;
    private EditText currentCKWValueEdit;
    private View currentCKWRemoveButton;
    private Button CKWAddButton;
    private JSONObject CKW;

    private Button filterButton;
    private Button resetButton;

    private PlacementRequestParams params;
    private TextWatcher textWatcher;

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
        displaySwitch = contentView.findViewById(R.id.display_switch);
        impressionSwitch = contentView.findViewById(R.id.impression_switch);
        rotateSwitch = contentView.findViewById(R.id.rotate_switch);

        displaySwitch.setChecked(true);
        impressionSwitch.setChecked(false);
        rotateSwitch.setChecked(false);

        CKWContainer = contentView.findViewById(R.id.ckw_container);

        CKWAddButton = contentView.findViewById(R.id.ckw_add_button);
        CKWAddButton.setEnabled(false);

        CKW = new JSONObject();


        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!currentCKWKeyEdit.getText().toString().isEmpty() && !currentCKWValueEdit.getText().toString().isEmpty()) {
                    CKWAddButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };


        CKWAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadCustomKeywordsRow(null, null);
            }
        });


        if (params == null) {
            resetFilters();
        } else {

            networkIdEdit.setText(String.valueOf(params.getNetworkId()));
            zoneIdEdit.setText(String.valueOf(params.getZoneId()));
            userIdEdit.setText(String.valueOf(params.getUserId() != null ? params.getUserId() : ""));

            String joined = TextUtils.join(",", params.getKeywords());
            keywordsEdit.setText(joined);

            int width = params.getWidth() ;
            int height = params.getHeight();

            widthEdit.setText(String.valueOf(width));
            heightEdit.setText(String.valueOf(height));


            JSONObject customKeywords = params.getCustomKeywords();

            if (customKeywords != null) {
                //Generate custom keyword section
                Iterator<String> keys = customKeywords.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    try {

                        loadCustomKeywordsRow(key, (String) customKeywords.get(key));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            loadCustomKeywordsRow(null, null);
            networkIdEdit.requestFocus();

        }

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] keywords = new String[0];
                if (!keywordsEdit.getText().toString().equals("")) {
                    List<String> keywordsList = Arrays.asList(keywordsEdit.getText().toString().split("\\s*,\\s*"));
                    keywords = keywordsList.toArray(new String[keywordsList.size()]);
                }

                int width = Integer.parseInt(widthEdit.getText().toString());
                int height = Integer.parseInt(heightEdit.getText().toString());
                long networkId;
                long zoneId;

//                try {
//
//                    width = Integer.valueOf(widthEdit.getText().toString());
//                    width = (int) (width * scale + 0.5f);
//                } catch (Exception e) {
//                    width = Resources.getSystem().getDisplayMetrics().widthPixels;
//
//                }
//
//
//                try {
//                    height = Math.min(Integer.parseInt(heightEdit.getText().toString()), 400);
//                    height = (int) (height * scale + 0.5f);
//
//                } catch (Exception e) {
//                    height = 400;
//                }

                try {

                    networkId = Long.valueOf(networkIdEdit.getText().toString());
                    zoneId = Long.valueOf(zoneIdEdit.getText().toString());

                } catch (Exception e) {
                    showErrorMessage();
                    return;
                }


                PlacementRequestParams params = new PlacementRequestParams(getActivity(),
                        networkId,
                        zoneId,
                        userIdEdit.getText().toString(),
                        keywords,
                        width,
                        height,
                        CKW
                );

                listener.onFilter(params, displaySwitch.isChecked(), impressionSwitch.isChecked(), rotateSwitch.isChecked());
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

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

    }

    private void showErrorMessage() {

        new AlertDialog.Builder(getActivity())
                .setTitle("Warning")
                .setMessage("The Network and Zone Ids are required")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetFilters();
                    }
                })
                .show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (FilterActivity) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (FilterActivity) context;
    }

    public void setParams(PlacementRequestParams params) {
        this.params = params;
    }

    public interface OnFilterListener {

        void onFilter(PlacementRequestParams params, boolean displayAd, boolean recordImpression, boolean rotate);
    }


    private void resetFilters() {
        int widthPixels = 400;
        int heightPixels = 400;

//        networkIdEdit.setText(String.valueOf(getResources().getInteger(R.integer.adyo_network_id)));
//        zoneIdEdit.setText(String.valueOf(getResources().getInteger(R.integer.adyo_zone_id_1)));
//        userIdEdit.setText("");
//        keywordsEdit.setText("");
//        widthEdit.setText(String.valueOf(widthPixels));
//        heightEdit.setText(String.valueOf(heightPixels));


        networkIdEdit.setText("2");
        zoneIdEdit.setText("197");
        userIdEdit.setText("");
        keywordsEdit.setText("d6_school_436");
        widthEdit.setText(String.valueOf(widthPixels));
        heightEdit.setText(String.valueOf(heightPixels));

        CKWContainer.removeAllViews();
        loadCustomKeywordsRow(null, null);

        networkIdEdit.requestFocus();
    }


    private void loadCustomKeywordsRow(@Nullable String key, @Nullable String value) {
        if (currentCKWRemoveButton != null)
            currentCKWRemoveButton.setVisibility(View.VISIBLE);


        View customKeywords = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_custom_keywords, null);

        //Add previous values to hash map
        if (currentCKWKeyEdit != null) {
            currentCKWKeyEdit.setEnabled(false);
            currentCKWValueEdit.setEnabled(false);

            try {
                CKW.put(currentCKWKeyEdit.getText().toString(), currentCKWValueEdit.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        currentCKWKeyEdit = customKeywords.findViewById(R.id.ckw_key);
        currentCKWValueEdit = customKeywords.findViewById(R.id.ckw_value);
        currentCKWRemoveButton = customKeywords.findViewById(R.id.ckw_remove_button);

        if (CKW.length() != 0)
            currentCKWKeyEdit.requestFocus();


        currentCKWKeyEdit.addTextChangedListener(textWatcher);
        currentCKWValueEdit.addTextChangedListener(textWatcher);


        currentCKWRemoveButton.setTag(customKeywords);

        currentCKWRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View customKeywordsView = (View) view.getTag();

                if (CKW.length() != 0) {
                    CKW.remove(((EditText) customKeywordsView.findViewById(R.id.ckw_key)).getText().toString());
                    CKWContainer.removeView(customKeywordsView);
                    CKWAddButton.setEnabled(false);

                }




            }
        });

        CKWContainer.addView(customKeywords);

        CKWAddButton.setEnabled(false);

        if (key != null && value != null) {
            currentCKWKeyEdit.setText(key);
            currentCKWValueEdit.setText(value);
        }

    }
}


