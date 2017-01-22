package com.matejdro.wearutils.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.matejdro.wearutils.miscutils.HtmlCompat;

/**
 * <p>SwitchPreference that will:</p>
 *
 * <p>1. Obey HTML tags in summary</p>
 * <p>2. Display title in two lines if it can't fit into one</p>
 */
public class SwitchPreferenceEx extends SwitchPreference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwitchPreferenceEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    public SwitchPreferenceEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public SwitchPreferenceEx(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public SwitchPreferenceEx(Context context) {
        super(context);

        init();
    }

    private void init()
    {
        setSummaryOff(HtmlCompat.fromHtml(getSummaryOff().toString()));
        setSummaryOn(HtmlCompat.fromHtml(getSummaryOn().toString()));
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView textView = (TextView) view.findViewById(android.R.id.title);
        if (textView != null) {
            textView.setSingleLine(false);
        }

    }
}
