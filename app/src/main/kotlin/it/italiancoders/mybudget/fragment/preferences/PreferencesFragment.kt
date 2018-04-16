package it.italiancoders.mybudget.fragment.preferences

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.mynameismidori.currencypicker.CurrencyPreference
import it.italiancoders.mybudget.R
import it.italiancoders.mybudget.activity.MainActivity_
import org.androidannotations.annotations.AfterPreferences
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.PreferenceByKey
import org.androidannotations.annotations.PreferenceScreen


/**
 * @author fattazzo
 *         <p/>
 *         date: 29/03/18
 */
@PreferenceScreen(R.xml.preferences)
@EFragment
open class PreferencesFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    @PreferenceByKey(R.string.pref_key_language)
    internal lateinit var langPref: ListPreference

    @PreferenceByKey(R.string.pref_key_currency)
    internal lateinit var currencyPref: ListPreference

    @AfterPreferences
    fun initPrefs() {
        updateAllPrefsSummary()

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this@PreferencesFragment)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
        updatePreferenceSummary(findPreference(key))

        if ("language" == key) {
            MaterialDialog.Builder(activity!!)
                    .iconRes(R.drawable.translate)
                    .title(R.string.change_language_dialog_title)
                    .content(R.string.change_language_dialog_message)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no)
                    .onPositive { _, _ ->
                        val mStartActivity = Intent(activity, MainActivity_::class.java)
                        val mPendingIntentId = 123456
                        val mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId,
                                mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
                        val mgr = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
                        System.exit(0)
                    }
                    .show()
        }
    }

    private fun updateAllPrefsSummary() {
        updatePreferenceSummary(langPref)
        updatePreferenceSummary(currencyPref)
    }

    /**
     * Update preference summary
     *
     * @param preference preference to update
     */
    private fun updatePreferenceSummary(preference: Preference?) {
        when (preference) {
            is CurrencyPreference -> {
                val pref = preference as CurrencyPreference?
                pref!!.summary = pref.value
            }
            is ListPreference -> {
                val listPreference = preference as ListPreference?
                listPreference!!.summary = listPreference.entry
            }
            is EditTextPreference -> {
                val textPreference = preference as EditTextPreference?
                var value = textPreference!!.text
                val inputType = textPreference.editText.inputType
                if (inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD != 0 || inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD != 0) {
                    value = value.replace(".".toRegex(), "*")
                }
                textPreference.summary = value
            }
        }
    }
}
