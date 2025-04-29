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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.urbanmedic.testapp.databinding.FragmentLoginBinding
import ru.urbanmedic.testapp.db.SeedDao
import ru.urbanmedic.testapp.db.UrbanMedicDB
import ru.urbanmedic.testapp.model.Seed

class LoginFragment : Fragment() {

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

        binding.seed.addTextChangedListener {
            binding.seed.error = null
        }

        binding.loginBtn.setOnClickListener {

            if(binding.seed.text!!.isNotBlank()){

                val seedDao: SeedDao = UrbanMedicDB.getDatabase(requireActivity().application).seedDao()

                lifecycleScope.launch {

                    seedDao.logout()
                    seedDao.login(
                        Seed(binding.seed.text.toString())
                    )

                    requireActivity().setResult(Activity.RESULT_OK)
                    requireActivity().finish()

                }
            }
        }
    }
}