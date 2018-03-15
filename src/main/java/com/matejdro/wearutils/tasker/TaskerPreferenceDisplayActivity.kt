package com.matejdro.wearutils.tasker

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.matejdro.wearutils.preferences.BundleSharedPreferences

abstract class TaskerPreferenceDisplayActivity : TaskerSetupActivity() {
    var preferenceStorage: Bundle? = null
        private set
    protected lateinit var preferences: BundleSharedPreferences

    protected abstract val description: String
    protected open var preferencesBundlePrefix: String
        get() = preferences.prefix
        set(value) {
            preferences.prefix = value
        }

    protected val originalValues: SharedPreferences?
        get() = null

    override fun onPreviousTaskerOptionsLoaded(taskerOptions: Bundle): Boolean {
        preferenceStorage = taskerOptions
        initPreferences()
        return true
    }

    override fun onFreshTaskerSetup() {
        preferenceStorage = Bundle()
        initPreferences()
    }

    protected open fun initPreferences() {
        preferences = BundleSharedPreferences(originalValues, preferenceStorage)
    }

    protected fun save() {
        val preferenceStorage = preferenceStorage
                ?: throw IllegalStateException("Preferences not initialized")
        val intent = Intent()

        val description = description

        val stringPreferences = preferenceStorage
                .keySet()
                .filter { preferenceStorage[it] is String }

        TaskerPlugin.Setting.setVariableReplaceKeys(preferenceStorage,
                stringPreferences.toTypedArray())

        intent.putExtra(LocaleConstants.EXTRA_STRING_BLURB, description)


        onPreSave(preferenceStorage, intent)
        intent.putExtra(LocaleConstants.EXTRA_BUNDLE, preferenceStorage)

        setResult(Activity.RESULT_OK, intent)
    }

    protected open fun onPreSave(settingsBundle: Bundle, taskerIntent: Intent) {

    }

    override fun onBackPressed() {
        save()
        super.onBackPressed()
    }
}
