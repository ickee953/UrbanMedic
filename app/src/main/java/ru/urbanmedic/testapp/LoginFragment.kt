/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 25 April 2025
 */

package ru.urbanmedic.testapp

import android.app.Activity
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
import ru.urbanmedic.testapp.utils.RefreshableUI
import ru.urbanmedic.testapp.utils.Utils.getLanguagePref
import ru.urbanmedic.testapp.utils.Utils.setLanguagePref
import ru.urbanmedic.testapp.utils.Utils.setLocale


class LoginFragment : Fragment(), RefreshableUI {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked)
                when(checkedId) {
                    R.id.ru -> {
                        setLocale(this, activity,"ru")
                        setLanguagePref(activity, "ru")
                    }
                    R.id.en -> {
                        setLocale(this, activity,"en")
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

    override fun onResume() {
        super.onResume()

        val locale = getLanguagePref(activity?.applicationContext)
        setLocale(this, activity,locale)

        when(locale){
            "ru" -> binding.toggleButton.check(R.id.ru)
            "en" -> binding.toggleButton.check(R.id.en)
        }
    }

    override fun refreshUI(){
        binding.enterSeedTextView.setText(R.string.seed)
        binding.textInputLayout.setHint(R.string.seed_hint)
        binding.seed.setHint(R.string.seed)
        binding.loginBtn.setText(R.string.sign_in)
    }
}