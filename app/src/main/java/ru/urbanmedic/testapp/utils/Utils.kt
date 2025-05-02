/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 30 April 2025
 */

package ru.urbanmedic.testapp.utils

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import kotlinx.coroutines.Job
import ru.urbanmedic.testapp.R
import java.util.Locale

object Utils {
    private const val PREFERENCES_NAME      = "PREFERENCES_NAME"
    private const val CURRENT_LOCALE_PREF   = "CURRENT_LOCALE_PREF"

    fun setLanguagePref(context: Context?, localeKey: String) {
        context?.getSharedPreferences(PREFERENCES_NAME, 0)?.edit() {
            putString(CURRENT_LOCALE_PREF, localeKey)
        }
    }

    fun getLanguagePref(context: Context?): String {
        val sp = context?.getSharedPreferences(PREFERENCES_NAME, 0)
        return sp?.getString(CURRENT_LOCALE_PREF, "en")!!
    }

    fun setLocale(refreshable: RefreshableUI, context: Activity?, lang: String) {
        val myLocale = Locale(lang)
        val res = context?.resources!!
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        Locale.setDefault(myLocale)
        refreshable.refreshUI()
        context.onConfigurationChanged(conf)
    }

    fun showDialog(
        activity: Activity?,
        title: String?,
        message: String?,
        positiveBtnStringId: Int,
        negativeBtnStringId: Int?,
        function: () -> Unit
    ) {
        val builder = activity?.let { AlertDialog.Builder(it) }
        builder?.setTitle(title)
        builder?.setMessage(message)
        builder?.setPositiveButton(positiveBtnStringId){ _, _ ->
            function()
        }
        if (negativeBtnStringId != null) {
            builder?.setNegativeButton(negativeBtnStringId){ _, _ ->}
        }
        builder?.show()
    }
}