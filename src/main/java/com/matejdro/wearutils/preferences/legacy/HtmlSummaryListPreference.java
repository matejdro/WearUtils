package com.matejdro.wearutils.preferences.legacy;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.matejdro.wearutils.miscutils.HtmlCompat;

public class HtmlSummaryListPreference extends ListPreference {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HtmlSummaryListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HtmlSummaryListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HtmlSummaryListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlSummaryListPreference(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        return HtmlCompat.fromHtml(super.getSummary().toString());
    }
}
