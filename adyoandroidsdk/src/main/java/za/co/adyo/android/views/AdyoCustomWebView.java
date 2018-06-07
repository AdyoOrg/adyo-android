package za.co.adyo.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * ClassName
 * Description
 *
 * @author UnitX, marilie
 * @version 1.0, 2/8/18
 */

public class AdyoCustomWebView extends WebView {
    public AdyoCustomWebView(Context context) {
        super(context);
    }

    public AdyoCustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdyoCustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onCheckIsTextEditor()
    {
        return true;
    }

}
