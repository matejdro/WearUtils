package com.matejdro.wearutils.preferences.compat

import android.content.Context
import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.util.AttributeSet
import com.matejdro.wearutils.miscutils.HtmlCompat

/**
 * Variant of [EditTextPreference] that automatically updates the preference's summary when
 * edited
 */
class AutoSummaryEditTextPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs), PreferenceWithDialog {
    private val summaryFormat: String = summary.toString()

    init {
        val summary = String.format(summaryFormat, text ?: "")
        setSummary(HtmlCompat.fromHtml(summary))
    }

    override fun setText(text: String?) {
        super.setText(text)

        val summary = String.format(summaryFormat, getText() ?: "")
        setSummary(HtmlCompat.fromHtml(summary))
    }

    override fun createDialog(key: String): PreferenceDialogFragmentCompat {
        return AutoSummaryEditTextPreferenceDialog.create(key)
    }

    class AutoSummaryEditTextPreferenceDialog : EditTextPreferenceDialogFragmentCompat() {
        companion object {
            fun create(key: String): AutoSummaryEditTextPreferenceDialog {
                val fragment = AutoSummaryEditTextPreferenceDialog()

                val arguments = Bundle(1)
                arguments.putString(PreferenceDialogFragmentCompat.ARG_KEY, key)

                fragment.arguments = arguments
                return fragment
            }
        }
    }
}
