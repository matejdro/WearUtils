package com.matejdro.wearutils.preferences.compat

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import androidx.preference.EditTextPreferenceDialogFragmentCompat
import androidx.preference.PreferenceDialogFragmentCompat
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
