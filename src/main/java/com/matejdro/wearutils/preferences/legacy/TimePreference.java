package com.matejdro.wearutils.preferences.legacy;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TimePicker;

import com.matejdro.wearutils.miscutils.HtmlCompat;

import java.util.Calendar;

public class TimePreference extends Preference implements TimePickerDialog.OnTimeSetListener {
    private String summaryFormat;
    private int time = 0;
    private java.text.DateFormat timeFormat;

    public TimePreference(Context context) {
        super(context, null);
        init();
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        summaryFormat = getSummary().toString();

        timeFormat = DateFormat.getTimeFormat(getContext());
    }

    @Override
    protected void onClick() {
        int hour = time / 60;
        int minute = time % 60;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), this, hour, minute, DateFormat.is24HourFormat(getContext()));
        timePickerDialog.show();


    }

    private void updateSummary() {
        int hour = time / 60;
        int minute = time % 60;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        String formattedTime = timeFormat.format(calendar.getTime());
        String summary = String.format(summaryFormat, formattedTime);
        setSummary(HtmlCompat.fromHtml(summary));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        time = restoreValue ? getPersistedInt(0) : (Integer) defaultValue;
        updateSummary();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time = hourOfDay * 60 + minute;
        persistInt(time);
        updateSummary();
        notifyChanged();
    }
}
