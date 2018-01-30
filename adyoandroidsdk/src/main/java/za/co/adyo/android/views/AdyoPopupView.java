package za.co.adyo.android.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import za.co.adyo.android.R;


/**
 * AdyoPopupView
 * Dialog with a WebView to display click url in a popup.
 *
 * @author UnitX, marilie
 * @version 1.0, 1/29/18
 */

public class AdyoPopupView extends DialogFragment {

    private View view;
    private AlertDialog dialog;

    public static AdyoPopupView newInstance(String url, @StyleRes int theme) {
        AdyoPopupView frag = new AdyoPopupView();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putInt("theme", theme);
        frag.setArguments(args);
        return frag;
    }

    public static AdyoPopupView newInstance(String url) {
        AdyoPopupView frag = new AdyoPopupView();
        Bundle args = new Bundle();
        args.putString("url", url);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String url = getArguments().getString("url");
        @StyleRes final int theme = getArguments().getInt("theme", -1);

        AlertDialog.Builder alertDialogBuilder =
                theme == -1
                        ? new AlertDialog.Builder(getActivity())
                        : new AlertDialog.Builder(getActivity(), theme);


        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_adyo_popup, null);
        WebView webView = view.findViewById(R.id.adyo_web_view);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setInitialScale(1);

        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageFinished(WebView view, String url) {

                String command = "javascript:(function() {" +
                         "document.getElementsByTagName(\"body\")[0].style.backgroundColor = \"" + "#ffffff" + "\";" +
                        "})()";
                view.loadUrl(command);

                super.onPageFinished(view, url);
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }

            @RequiresApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false; // then it is not handled by default action
            }
        });

        webView.loadUrl(url);

        alertDialogBuilder.setPositiveButton(" ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null)
                {
                    dismiss();
                }
            }
        });





        alertDialogBuilder.setView(view);


        dialog = alertDialogBuilder.create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                // if you do the following it will be left aligned, doesn't look
                // correct
                // button.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play,
                // 0, 0, 0);

                Drawable drawable = getActivity().getResources().getDrawable(
                        R.drawable.adyo_ic_close);


                drawable.setColorFilter(button.getTextColors().getDefaultColor(), PorterDuff.Mode.SRC_ATOP);


                // set the bounds to place the drawable a bit right
                drawable.setBounds((int) (drawable.getIntrinsicWidth() * 0.5),
                        0, (int) (drawable.getIntrinsicWidth() * 1.5),
                        drawable.getIntrinsicHeight());
                button.setCompoundDrawables(null, null, drawable, null);

            }
        });




        return dialog;
    }



}
