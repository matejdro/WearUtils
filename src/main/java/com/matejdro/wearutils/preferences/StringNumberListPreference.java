package com.matejdro.wearutils.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

public class StringNumberListPreference extends StringListPreference {
    public StringNumberListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public StringNumberListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StringNumberListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StringNumberListPreference(Context context) {
        super(context);
    }

    @Override
    protected void preAddDialogOpen(AlertDialog.Builder builder, EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
