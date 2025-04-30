/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 25 April 2025
 */

package ru.urbanmedic.testapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.urbanmedic.testapp.databinding.FragmentLoginBinding
import ru.urbanmedic.testapp.db.SeedDao
import ru.urbanmedic.testapp.db.UrbanMedicDB
import ru.urbanmedic.testapp.model.Seed
import java.util.Locale
import androidx.core.content.edit


class LoginFragment : Fragment() {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        updateUI()
        super.onConfigurationChanged(newConfig)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked)
                when(checkedId) {
                    R.id.ru -> {
                        setLocale("ru")
                        setLanguagePref(activity, "ru")
                    }
                    R.id.en -> {
                        setLocale("en")
                        setLanguagePref(activity, "en")
                    }
                }
        }

        binding.loginBtn.isEnabled = false

        binding.seed.doOnTextChanged { inputText, _, _, _ ->
            binding.loginBtn.isEnabled = inputText!!.isNotBlank()
        }

        binding.loginBtn.setOnClickListener {
            val seedDao: SeedDao = UrbanMedicDB.getDatabase(requireActivity().application).seedDao()

            lifecycleScope.launch {
                seedDao.logout()
                seedDao.login(
                    Seed(binding.seed.text.toString())
                )

                activity?.let{
                    it.setResult(Activity.RESULT_OK)
                    it.finish()
                }
            }
        }
    }

    val prefName = "preferenceName"
    val langPref = "currentLang"

    private fun setLanguagePref(mContext: Context?, localeKey: String) {
        mContext?.getSharedPreferences(prefName, 0)?.edit() {
            putString(langPref, localeKey)
        }
        //val appPreference: AppPreference = AppPreference(mContext)
        //appPreference.setLanguage(localeKey)
    }

    fun getLanguagePref(mContext: Context?): String {
        val sp = mContext?.getSharedPreferences(prefName, 0)
        return sp?.getString(langPref, "en")!!
        //val appPreference: AppPreference = AppPreference(mContext)
        //return appPreference.getLanguage()
    }

    override fun onResume() {
        val locale = getLanguagePref(activity?.applicationContext)
        setLocale(locale)
        when(locale){
            "ru" -> binding.toggleButton.check(R.id.ru)
            "en" -> binding.toggleButton.check(R.id.en)
        }

        super.onResume()
    }

    private fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        Locale.setDefault(myLocale)
        onConfigurationChanged(conf)
    }

    private fun updateUI(){
        binding.enterSeedTextView.setText(R.string.seed)
        binding.textInputLayout.setHint(R.string.seed_hint)
        binding.seed.setHint(R.string.seed)
        binding.loginBtn.setText(R.string.sign_in)
    }
}