package com.matejdro.wearutils.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;

import com.matejdro.wearutils.miscutils.HtmlCompat;

public class AutoSummaryEditTextPreference extends EditTextPreference implements Preference.OnPreferenceChangeListener
{
    private String summaryFormat;

    public AutoSummaryEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AutoSummaryEditTextPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public AutoSummaryEditTextPreference(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        summaryFormat = getSummary().toString();

        setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
    {
        super.onSetInitialValue(restoreValue, defaultValue);
        onPreferenceChange(this, getText());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String summary = String.format(summaryFormat, (String) newValue);
        setSummary(HtmlCompat.fromHtml(summary));
        return true;
    }
}
