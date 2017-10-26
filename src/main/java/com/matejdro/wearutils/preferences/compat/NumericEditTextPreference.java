package com.matejdro.wearutils.preferences.compat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.matejdro.wearutils.miscutils.HtmlCompat;

public class NumericEditTextPreference extends EditTextPreference implements PreferenceWithDialog {
    private String summaryFormat;

    public NumericEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        summaryFormat = getSummary().toString();
    }


    @Override
    public void setText(String text) {
        super.setText(text);

        String summary = String.format(summaryFormat, getText());
        setSummary(HtmlCompat.fromHtml(summary));
    }

    @Override
    public PreferenceDialogFragmentCompat createDialog(String key) {
        return NumericEditTextPreferenceDialog.create(key);
    }

    public static class NumericEditTextPreferenceDialog extends
            EditTextPreferenceDialogFragmentCompat {

        public static NumericEditTextPreferenceDialog create(String key) {
            NumericEditTextPreferenceDialog fragment = new NumericEditTextPreferenceDialog();

            Bundle arguments = new Bundle(1);
            arguments.putString(ARG_KEY, key);

            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        protected void onBindDialogView(View view) {
            super.onBindDialogView(view);

            EditText editBox = view.findViewById(android.R.id.edit);
            editBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }
}
