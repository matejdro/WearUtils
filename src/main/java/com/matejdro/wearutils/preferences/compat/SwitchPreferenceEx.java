package com.matejdro.wearutils.preferences.compat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreferenceCompat;

/**
 * <p>SwitchPreference that will:</p>
 * <p>
 * <p>1. Obey HTML tags in summary</p>
 * <p>2. Display title in two lines if it can't fit into one</p>
 */
public class SwitchPreferenceEx extends SwitchPreferenceCompat {
    public SwitchPreferenceEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        titleView.setSingleLine(false);

        super.onBindViewHolder(holder);
    }
}
