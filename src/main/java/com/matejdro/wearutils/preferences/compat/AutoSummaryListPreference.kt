package com.matejdro.wearutils.preferences.compat

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference


/**
 * Variant of ListPreference that will automatically set its summary to the currently selected value
 */
class AutoSummaryListPreference : ListPreference {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    init {
        summary = getCurrentlySelectedValueDescription()
    }

    private fun getCurrentlySelectedValueDescription(): CharSequence? {
        val index = entryValues.indexOf(value).coerceAtLeast(0)

        return entries.elementAt(index)
    }

    override fun setValue(value: String?) {
        super.setValue(value)

        summary = getCurrentlySelectedValueDescription()
    }
}
