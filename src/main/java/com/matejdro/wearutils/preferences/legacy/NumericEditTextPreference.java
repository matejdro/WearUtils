package com.matejdro.wearutils.preferences.legacy;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.text.InputType;
import android.util.AttributeSet;

import com.matejdro.wearutils.miscutils.HtmlCompat;

public class NumericEditTextPreference extends EditTextPreference implements Preference.OnPreferenceChangeListener {
    private String summaryFormat;

    public NumericEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NumericEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumericEditTextPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        summaryFormat = getSummary().toString();

        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        setOnPreferenceChangeListener(this);
    }


    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        onPreferenceChange(this, getText());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String summary = String.format(summaryFormat, (String) newValue);
        setSummary(HtmlCompat.fromHtml(summary));
        return true;
    }
}
